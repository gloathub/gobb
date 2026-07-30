(ns gobb.cli
  (:require [babashka.impl.exceptions]
            [babashka.pods :as pods]
            [gobb.host :as host]
            [gobb.project :as project]
            [gobb.repl :as repl]
            [gobb.servers :as servers]
            [gobb.version]
            [babashka.tasks :as tasks]))

(def usage
  "Usage: gobb [PROJECT-OPTS] [--init FILE] [-cp PATH|--classpath PATH] -e EXPR [ARGS...]\n       gobb [PROJECT-OPTS] [--init FILE] [-cp PATH|--classpath PATH] -m NS|VAR [ARGS...]\n       gobb [PROJECT-OPTS] [--init FILE] [-cp PATH|--classpath PATH] -x VAR [ARGS...]\n       gobb [PROJECT-OPTS] [-cp PATH|--classpath PATH] --repl\n       gobb [PROJECT-OPTS] --socket-repl [[HOST:]PORT]\n       gobb [PROJECT-OPTS] --nrepl-server [[HOST:]PORT]\n       gobb [PROJECT-OPTS] [--init FILE] [-cp PATH|--classpath PATH] FILE [ARGS...]\n       gobb [PROJECT-OPTS] run [--parallel] [--prn] TASK [ARGS...]\n       gobb [PROJECT-OPTS] tasks\n       gobb [PROJECT-OPTS] build INPUT -o OUTPUT [--platform OS/ARCH]\n       SOURCE | gobb\n\nPROJECT-OPTS:\n  --config FILE       Use an explicit bb.edn or deps.edn\n  --deps-root DIR     Resolve relative project paths from DIR\n  -Sdeps EDN          Merge dependency EDN after project configuration\n  -A ALIASES          Apply comma- or colon-separated aliases")

(def build-usage
  "Usage: gobb build INPUT -o OUTPUT [--platform OS/ARCH]")

(defn fail! [message]
  (fmt.Fprintln os.Stderr (str "gobb: " message))
  (fmt.Fprintln os.Stderr usage)
  (host/exit! 1))

(defn parse-global-options [argv]
  (loop [args argv
         options {:classpath nil
                  :init nil
                  :config nil
                  :deps-root nil
                  :merge-deps nil
                  :aliases []}]
    (cond
      (contains? #{"-cp" "--classpath"} (first args))
      (if-let [path (second args)]
        (recur (drop 2 args) (assoc options :classpath path))
        (fail! (str (first args) " requires a path")))

      (= "--init" (first args))
      (if-let [file (second args)]
        (recur (drop 2 args) (assoc options :init file))
        (fail! "--init requires a file"))

      (= "--config" (first args))
      (if-let [file (second args)]
        (recur (drop 2 args) (assoc options :config file))
        (fail! "--config requires a file"))

      (= "--deps-root" (first args))
      (if-let [directory (second args)]
        (recur (drop 2 args) (assoc options :deps-root directory))
        (fail! "--deps-root requires a directory"))

      (= "-Sdeps" (first args))
      (if-let [deps (second args)]
        (recur (drop 2 args) (assoc options :merge-deps deps))
        (fail! "-Sdeps requires EDN"))

      (= "-A" (first args))
      (if-let [aliases (second args)]
        (recur
         (drop 2 args)
         (update options :aliases into
                 (remove empty?
                         (strings.FieldsFunc
                          aliases
                          #(contains? #{\, \:} %)))))
        (fail! "-A requires aliases"))

      (and (first args)
           (.startsWith (str (first args)) "-A:"))
      (recur
       (next args)
       (update options :aliases into
               (remove empty?
                       (strings.Split (subs (first args) 3) ":"))))

      :else
      (assoc options :argv args))))

(defn configure-classpath! [paths]
  ;; BB always loads source from the working directory. Explicit classpath
  ;; and resolved project entries are searched after it.
  (host/add-load-path! ".")
  (doseq [path paths]
    (host/add-load-path! path))
  (host/install-data-readers!)
  (System/setProperty "java.class.path"
                      (project/configured-classpath-string)))

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
            (project/stage-classpath! temporary-dir)
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
              args (cond-> [gloat temporary-dir
                            (str "--out=" output)
                            "--force"
                            "--quiet"
                            "--ext=goimports"]
                     target-option (conj target-option))
              command (apply os:exec.Command args)
              _ (set! (.Env command)
                      (apply
                       go/append
                       (go/make (go/slice-of go/string) 0)
                       (concat
                        (os.Environ)
                        [(str "GLJ_CLASSPATH=" temporary-dir)])))
              [command-output command-error] (.CombinedOutput command)]
          (when command-error
            (let [details (strings.TrimSpace (go/string command-output))]
              (os.RemoveAll temporary-dir)
              (build-fail!
               (str "Gloat failed"
                    (if (empty? details)
                      (str ": " (fmt.Sprint command-error))
                      (str ":\n" details))))))
          (os.RemoveAll temporary-dir)
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
  (github.com:glojurelang:glojure:pkg:repl.IsTerminal))

(defn stdin-character-device? []
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

(defn start-enhanced-repl []
  (host/set-command-line-args! ())
  (host/set-file! host/repl-source-path)
  (println (str "Gobb v" gobb.version/version))
  (println (str "Babashka v" gobb.version/babashka-version))
  (println "Type :repl/help for help")
  (let [previous (os.Getenv "GLJ_REPL_NO_BANNER")
        configured-history (os.Getenv "GOBB_HISTORY_FILE")
        [home home-error] (os.UserHomeDir)
        history-file
        (if-not (empty? configured-history)
          configured-history
          (if home-error
            ".gobb_history"
            (path:filepath.Join home ".gobb_history")))
        history-option
        (github.com:glojurelang:glojure:pkg:repl.WithHistoryFile
         history-file "jline")]
    (os.Setenv "GLJ_REPL_NO_BANNER" "all")
    (try
      (github.com:glojurelang:glojure:pkg:repl.Start history-option)
      (finally
        (if (empty? previous)
          (os.Unsetenv "GLJ_REPL_NO_BANNER")
          (os.Setenv "GLJ_REPL_NO_BANNER" previous))))))

(defn server-address [arguments default-port]
  (let [candidate (second arguments)]
    (if (and candidate
             (not (.startsWith (str candidate) "-")))
      candidate
      (str default-port))))

(defn run-task-command [arguments]
  (loop [arguments arguments
         options {:parallel false
                  :print-result false}]
    (cond
      (= "--parallel" (first arguments))
      (recur (next arguments) (assoc options :parallel true))

      (= "--prn" (first arguments))
      (recur (next arguments) (assoc options :print-result true))

      (and (first arguments)
           (.startsWith (str (first arguments)) "-"))
      (fail! (str "unknown run option: " (first arguments)))

      (empty? arguments)
      (fail! "run requires a task")

      :else
      (tasks/execute-task! (first arguments)
                           (rest arguments)
                           options))))

(defn -main [& argv]
  (host/initialize!)
  (System/setProperty
   "babashka.version" gobb.version/babashka-version)
  (host/run-main!
   (fn []
     (let [{:keys [argv init] :as options}
           (parse-global-options argv)
           paths (project/configure! options)]
       (configure-classpath! paths)
       (pods/load-configured-pods! (project/configured-pods))
       (host/run-preloads!)
       (host/run-init! init)
       (cond
         (empty? argv)
         (cond
           (stdin-terminal?)
           (start-enhanced-repl)

           (stdin-character-device?)
           (start-repl)

           :else
           (host/evaluate-source
            (slurp *in*)
            {:file host/no-source-path
             :print-result? true}))

         (= "--repl" (first argv))
         (if (stdin-terminal?)
           (start-enhanced-repl)
           (start-repl))

         (contains? #{"--socket-repl" "socket-repl"}
                    (first argv))
         (servers/serve! :socket-repl
                         (server-address argv 1666))

         (contains? #{"--nrepl-server" "nrepl-server"}
                    (first argv))
         (servers/serve! :nrepl
                         (server-address argv 1667))

         (= "build" (first argv))
         (build-program (rest argv))

         (= "prepare" (first argv))
         nil

         (= "print-deps" (first argv))
         (let [arguments (rest argv)]
           (cond
             (empty? arguments)
             (prn (project/printable-deps))

             (and (= "--format" (first arguments))
                  (= "classpath" (second arguments))
                  (= 2 (count arguments)))
             (println (project/classpath-string))

             (and (= "--format" (first arguments))
                  (= "deps" (second arguments))
                  (= 2 (count arguments)))
             (prn (project/printable-deps))

             :else
             (fail! "print-deps expects --format deps|classpath")))

         (= "run" (first argv))
         (run-task-command (rest argv))

         (= "tasks" (first argv))
         (tasks/list-tasks!)

         (= "doc" (first argv))
         (if-let [task-name (second argv)]
           (tasks/task-doc! task-name)
           (fail! "doc requires a task"))

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

         (and (project/exists? (first argv))
              (not (.startsWith (str (first argv)) "-")))
         (evaluate-file (first argv) (rest argv))

         (tasks/task-defined? (first argv))
         (tasks/execute-task! (first argv) (rest argv))

         (.startsWith (str (first argv)) "-")
         (fail! (str "unknown option: " (first argv)))

         :else
         (evaluate-file (first argv) (rest argv)))))))
