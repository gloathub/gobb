(ns gobb.cli
  (:require [babashka.impl.exceptions]
            [gobb.host :as host]
            [gobb.repl :as repl]
            [gobb.version]))

(def usage
  "Usage: gobb [--init FILE] [-cp PATH|--classpath PATH] -e EXPR [ARGS...]\n       gobb [--init FILE] [-cp PATH|--classpath PATH] -m NS|VAR [ARGS...]\n       gobb [--init FILE] [-cp PATH|--classpath PATH] -x VAR [ARGS...]\n       gobb [-cp PATH|--classpath PATH] --repl\n       gobb [--init FILE] [-cp PATH|--classpath PATH] FILE [ARGS...]\n       gobb build INPUT -o OUTPUT [--platform OS/ARCH]\n       SOURCE | gobb")

(def build-usage
  "Usage: gobb build INPUT -o OUTPUT [--platform OS/ARCH]")

(defn fail! [message]
  (fmt.Fprintln os.Stderr (str "gobb: " message))
  (fmt.Fprintln os.Stderr usage)
  (host/exit! 1))

(defn parse-global-options [argv]
  (loop [args argv
         classpath ""
         init nil]
    (cond
      (contains? #{"-cp" "--classpath"} (first args))
      (if-let [path (second args)]
        (recur (drop 2 args) path init)
        (fail! (str (first args) " requires a path")))

      (= "--init" (first args))
      (if-let [file (second args)]
        (recur (drop 2 args) classpath file)
        (fail! "--init requires a file"))

      :else
      {:argv args
       :classpath classpath
       :init init})))

(defn configure-classpath! [classpath]
  ;; BB always loads source from the working directory. Explicit classpath
  ;; entries are searched after it in platform path-list order.
  (host/add-load-path! ".")
  (doseq [path (path:filepath.SplitList classpath)]
    (when-not (empty? path)
      (host/add-load-path! path)))
  (host/install-data-readers!)
  (System/setProperty "java.class.path" classpath))

(defn build-fail! [message]
  (fmt.Fprintln os.Stderr (str "gobb build: " message))
  (fmt.Fprintln os.Stderr build-usage)
  (host/exit! 1))

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
        (let [[input-bytes input-error] (os.ReadFile input)]
          (when input-error
            (build-fail! (str "cannot read INPUT: " input-error)))
          (let [[temporary-dir temporary-error]
                (os.MkdirTemp "" "gobb-build-")]
            (when temporary-error
              (build-fail!
               (str "cannot create temporary directory: "
                    temporary-error)))
            (let [staged-input (path:filepath.Join
                                temporary-dir "main.clj")
                  staged-source
                  (host/prepare-build-source
                   (fmt.Sprintf "%s" input-bytes))
                  write-error
                  (os.WriteFile staged-input
                                (.getBytes staged-source)
                                0644)]
              (when write-error
                (os.RemoveAll temporary-dir)
                (build-fail!
                 (str "cannot stage INPUT: " write-error)))
              (let [gloat (or (os.Getenv "GOBB_GLOAT") "gloat")
              target-option (when platform
                              (if (= "js/wasm" platform)
                                "--to=js"
                                (str "--platform=" platform)))
              args (cond-> [gloat staged-input
                            (str "--out=" output)
                            "--force"
                            "--quiet"
                            "--ext=goimports"]
                     target-option (conj target-option))
              command (apply os:exec.Command args)
              [command-output command-error] (.CombinedOutput command)]
          (os.RemoveAll temporary-dir)
          (when command-error
            (let [details (strings.TrimSpace (go/string command-output))]
              (build-fail!
               (str "Gloat failed"
                    (if (empty? details)
                      (str ": " (fmt.Sprint command-error))
                      (str ":\n" details))))))
                output))))))))

(defn evaluate-expression [expression args]
  (host/evaluate-source
   expression
   {:args args
    :file host/no-source-path
    :print-result? true}))

(defn evaluate-file [file args]
  (let [[absolute-file error] (path:filepath.Abs file)]
    (when error
      (fail! (str "cannot resolve file path: " error)))
    (System/setProperty "babashka.file" absolute-file)
    (host/evaluate-source
     (slurp absolute-file)
     {:args args
      :file absolute-file})))

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
  (host/set-command-line-args! ())
  (host/set-file! host/repl-source-path)
  (repl/start read-native-form))

(defn -main [& argv]
  (host/initialize!)
  (System/setProperty
   "babashka.version" gobb.version/babashka-version)
  (host/run-main!
   (fn []
     (let [{:keys [argv classpath init]} (parse-global-options argv)]
       (configure-classpath! classpath)
       (host/run-preloads!)
       (host/run-init! init)
       (cond
         (empty? argv)
         (if (stdin-terminal?)
           (start-repl)
           (host/evaluate-source
            (slurp *in*)
            {:file host/no-source-path
             :print-result? true}))

         (= "--repl" (first argv))
         (start-repl)

         (= "build" (first argv))
         (build-program (rest argv))

         (contains? #{"-e" "--eval"} (first argv))
         (if-let [expression (second argv)]
           (evaluate-expression expression (drop 2 argv))
           (fail! (str (first argv) " requires an expression")))

         (contains? #{"-m" "--main"} (first argv))
         (if-let [target (second argv)]
           (host/invoke-main! target (drop 2 argv))
           (fail! (str (first argv) " requires a namespace or var")))

         (contains? #{"-x" "--exec"} (first argv))
         (if-let [target (second argv)]
           (host/invoke-exec! target (drop 2 argv))
           (fail! (str (first argv) " requires a var")))

         (or (= "-h" (first argv))
             (= "--help" (first argv)))
         (println usage)

         (= "--version" (first argv))
         (println (str "gobb v" gobb.version/version))

         (.startsWith (str (first argv)) "-")
         (fail! (str "unknown option: " (first argv)))

         :else
         (evaluate-file (first argv) (rest argv)))))))
