(ns gobb.cli
  (:require [babashka.impl.exceptions]
            [gobb.repl :as repl]
            [gobb.version]))

(def usage
  "Usage: gobb [-cp PATH|--classpath PATH] -e EXPR [ARGS...]\n       gobb [-cp PATH|--classpath PATH] --repl\n       gobb [-cp PATH|--classpath PATH] FILE [ARGS...]\n       gobb build INPUT -o OUTPUT [--platform OS/ARCH]\n       SOURCE | gobb")

(def build-usage
  "Usage: gobb build INPUT -o OUTPUT [--platform OS/ARCH]")

(defn fail! [message]
  (fmt.Fprintln os.Stderr (str "gobb: " message))
  (fmt.Fprintln os.Stderr usage)
  (os.Exit 1))

(defn set-command-line-args! [args]
  (alter-var-root #'*command-line-args* (constantly (seq args))))

(defn parse-global-options [argv]
  (loop [args argv
         classpath ""]
    (if (contains? #{"-cp" "--classpath"} (first args))
      (if-let [path (second args)]
        (recur (drop 2 args) path)
        (fail! (str (first args) " requires a path")))
      {:argv args
       :classpath classpath})))

(defn configure-classpath! [classpath]
  ;; BB always loads source from the working directory. Explicit classpath
  ;; entries are searched after it in platform path-list order.
  (add-load-path ".")
  (doseq [path (path:filepath.SplitList classpath)]
    (when-not (empty? path)
      (add-load-path path)))
  (System/setProperty "java.class.path" classpath))

(defn build-fail! [message]
  (fmt.Fprintln os.Stderr (str "gobb build: " message))
  (fmt.Fprintln os.Stderr build-usage)
  (os.Exit 1))

(defn parse-build-options [argv]
  (loop [args argv
         options {}]
    (if-let [arg (first args)]
      (cond
        (contains? #{"-h" "--help"} arg)
        (assoc options :help true)

        (contains? #{"-o" "--out"} arg)
        (if-let [output (second args)]
          (recur (drop 2 args) (assoc options :output output))
          (build-fail! (str arg " requires a path")))

        (= "--platform" arg)
        (if-let [platform (second args)]
          (recur (drop 2 args) (assoc options :platform platform))
          (build-fail! "--platform requires OS/ARCH"))

        (.startsWith (str arg) "--platform=")
        (recur (next args)
               (assoc options :platform
                      (subs arg (count "--platform="))))

        (.startsWith (str arg) "-")
        (build-fail! (str "unknown option: " arg))

        (:input options)
        (build-fail! (str "unexpected argument: " arg))

        :else
        (recur (next args) (assoc options :input arg)))
      options)))

(defn build-program [argv]
  (let [{:keys [help input output platform]} (parse-build-options argv)]
    (if help
      (println build-usage)
      (do
        (when-not input
          (build-fail! "INPUT is required"))
        (when-not output
          (build-fail! "-o OUTPUT is required"))
        (let [gloat (or (os.Getenv "GOBB_GLOAT") "gloat")
              target-option (when platform
                              (if (= "js/wasm" platform)
                                "--to=js"
                                (str "--platform=" platform)))
              args (cond-> [gloat input
                            (str "--out=" output)
                            "--force"
                            "--quiet"
                            "--ext=goimports"]
                     target-option (conj target-option))
              command (apply os:exec.Command args)
              [command-output command-error] (.CombinedOutput command)]
          (when command-error
            (let [details (strings.TrimSpace (go/string command-output))]
              (build-fail!
               (str "Gloat failed"
                    (if (empty? details)
                      (str ": " (fmt.Sprint command-error))
                      (str ":\n" details))))))
          output)))))

(defn evaluate-source [source print-result?]
  ;; A single enclosing do lets the runtime reader accept any number of forms
  ;; while keeping them in the same Glojure environment.
  (let [form (read-string (str "(do\n" source "\n)"))
        result (eval form)]
    (when (and print-result? (some? result))
      (prn result))
    result))

(defn evaluate-expression [expression args]
  (set-command-line-args! args)
  (evaluate-source expression true))

(defn evaluate-file [file args]
  (let [[absolute-file error] (path:filepath.Abs file)]
    (when error
      (fail! (str "cannot resolve file path: " error)))
    (System/setProperty "babashka.file" absolute-file)
    (set-command-line-args! args)
    (evaluate-source (slurp absolute-file) false)))

(defn stdin-terminal? []
  (let [[info error] (.Stat os.Stdin)]
    (when error
      (fail! (str "cannot inspect standard input: " error)))
    (not (zero? (bit-and (int (.Mode info))
                         (int os.ModeCharDevice))))))

(def native-input (atom ""))

(defn read-input-chunk []
  (let [buffer (go/make (go/slice-of go/byte) 4096)
        [count error] (.Read os.Stdin buffer)]
    (cond
      (pos? count) (go/string (go/slice buffer 0 count))
      error nil
      :else "")))

(defn read-input-line []
  (loop []
    (let [buffer @native-input
          newline (strings.Index buffer "\n")]
      (if (not= -1 newline)
        (let [line (subs buffer 0 newline)]
          (reset! native-input (subs buffer (inc newline)))
          line)
        (if-let [chunk (read-input-chunk)]
          (do
            (swap! native-input str chunk)
            (recur))
          (when-not (empty? buffer)
            (reset! native-input "")
            buffer))))))

(defn complete-form? [source]
  (loop [characters (seq source)
         depth 0
         string? false
         escaped? false
         comment? false]
    (if-let [character (first characters)]
      (cond
        comment?
        (recur (next characters) depth string? false
               (not= character \newline))

        escaped?
        (recur (next characters) depth string? false false)

        (= character \\)
        (recur (next characters) depth string? true false)

        (= character \")
        (recur (next characters) depth (not string?) false false)

        string?
        (recur (next characters) depth true false false)

        (= character \;)
        (recur (next characters) depth false false true)

        (contains? #{\( \[ \{} character)
        (recur (next characters) (inc depth) false false false)

        (contains? #{\) \] \}} character)
        (recur (next characters) (dec depth) false false false)

        :else
        (recur (next characters) depth false false false))
      (and (not (pos? depth)) (not string?)))))

(defn read-native-form [request-prompt request-exit]
  (loop [source ""]
    (if-let [line (read-input-line)]
      (let [source (str source line "\n")]
        (if (empty? (strings.TrimSpace source))
          request-prompt
          (if (complete-form? source)
            (repl/handle-command
             (read-string source) request-prompt request-exit)
            (recur source))))
      request-exit)))

(defn start-repl []
  (reset! native-input "")
  (repl/start read-native-form))

(defn -main [& argv]
  (System/setProperty
   "babashka.version" gobb.version/babashka-version)
  (let [{:keys [argv classpath]} (parse-global-options argv)]
    (configure-classpath! classpath)
    (cond
      (empty? argv)
      (if (stdin-terminal?)
        (start-repl)
        (evaluate-source (slurp *in*) true))

      (= "--repl" (first argv))
      (start-repl)

      (= "build" (first argv))
      (build-program (rest argv))

      (= "-e" (first argv))
      (if-let [expression (second argv)]
        (evaluate-expression expression (drop 2 argv))
        (fail! "-e requires an expression"))

      (or (= "-h" (first argv))
          (= "--help" (first argv)))
      (println usage)

      (= "--version" (first argv))
      (println (str "gobb v" gobb.version/version))

      (.startsWith (str (first argv)) "-")
      (fail! (str "unknown option: " (first argv)))

      :else
      (evaluate-file (first argv) (rest argv)))))
