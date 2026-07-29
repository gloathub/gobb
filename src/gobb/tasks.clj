(ns babashka.tasks
  (:require [gobb.capabilities :as capabilities]
            [gobb.host :as host]
            [gobb.project :as project]))

(def ^:dynamic *task* nil)
(def ^:dynamic *execution* nil)
(def current-state (atom {}))

(defn current-task []
  *task*)

(defn task-config []
  (or (:tasks @project/resolved-config) {}))

(defn task-defined? [task-name]
  (contains? (task-config) (symbol (str task-name))))

(defn task-names []
  (let [configured (or (get @project/resolved-config
                            project/task-order-key)
                       [])
        discovered (->> (keys (task-config))
                        (filter symbol?))
        names (concat configured
                      (remove (set configured) discovered))]
    (remove #(strings.HasPrefix (str %) "-") names)))

(defn task-definition [task-name]
  (get (task-config) (symbol (str task-name))))

(defn task-private? [task-name definition]
  (or (strings.HasPrefix (str task-name) "-")
      (and (map? definition) (:private definition))))

(defn list-tasks! []
  (let [tasks (task-config)
        names (remove #(task-private? % (get tasks %))
                      (task-names))]
    (if (seq names)
      (let [width (apply max (map #(count (str %)) names))]
        (println "The following tasks are available:")
        (println)
        (doseq [task-name names
                :let [definition (get tasks task-name)
                      doc (when (map? definition) (:doc definition))
                      padding (apply str
                                     (repeat (- width
                                                (count (str task-name)))
                                             " "))]]
          (println
           (str task-name padding
                (when doc
                  (str " " (first (strings.Split (str doc) "\n"))))))))
      (println "No tasks found."))))

(defn task-doc! [task-name]
  (let [task-name (symbol (str task-name))
        definition (task-definition task-name)]
    (when-not (task-defined? task-name)
      (throw
       (ex-info (str "No such task: " task-name)
                {:babashka/exit 1
                 :task task-name})))
    (println "-------------------------")
    (println task-name)
    (println "Task")
    (when-let [doc (and (map? definition) (:doc definition))]
      (println doc))))

(defn tokenize-command [command]
  (loop [index 0
         token ""
         token? false
         quote nil
         escaped? false
         arguments []]
    (if (< index (count command))
      (let [character (.charAt command index)]
        (cond
          escaped?
          (recur (inc index) (str token character) true
                 quote false arguments)

          (and (not= quote \')
               (= character \\))
          (recur (inc index) token true quote true arguments)

          quote
          (if (= character quote)
            (recur (inc index) token true nil false arguments)
            (recur (inc index) (str token character) true
                   quote false arguments))

          (contains? #{\' \"} character)
          (recur (inc index) token true character false arguments)

          (Character/isWhitespace character)
          (if token?
            (recur (inc index) "" false nil false
                   (conj arguments token))
            (recur (inc index) token false nil false arguments))

          :else
          (recur (inc index) (str token character) true
                 nil false arguments)))
      (do
        (when quote
          (throw
           (ex-info "shell command contains an unterminated quote"
                    {:babashka/exit 1
                     :gobb/process :invalid-command})))
        (cond-> arguments
          token? (conj token))))))

(defn command-arguments [arguments]
  (let [arguments (mapv str arguments)]
    (cond
      (empty? arguments)
      (throw
       (ex-info "shell requires a command"
                {:babashka/exit 1
                 :gobb/process :missing-command}))

      (= 1 (count arguments))
      (tokenize-command (first arguments))

      :else arguments)))

(defn environment-entries [options]
  (let [base (if (contains? options :env)
               []
               (vec (os.Environ)))
        environment (merge (:env options)
                           (:extra-env options))]
    (let [entries
          (into base
                (map (fn [[key value]]
                       (str (name key) "=" value)))
                (or environment {}))]
      (apply go/append
             (go/make (go/slice-of go/string) 0)
             entries))))

(defn buffer-value [buffer mode]
  (when buffer
    (if (= :bytes mode)
      (.Bytes buffer)
      (.String buffer))))

(defn open-output-file [value append?]
  (let [flags (bit-or os.O_CREATE
                      os.O_WRONLY
                      (if append?
                        os.O_APPEND
                        os.O_TRUNC))
        [stream error]
        (os.OpenFile (str value) flags 0666)]
    (when error
      (throw
       (ex-info
        (str "Could not open process output " value
             ": " (fmt.Sprint error))
        {:gobb/process :redirect
         :path (str value)})))
    stream))

(defn process-exit-code [command error]
  (if (nil? error)
    0
    (let [state (.ProcessState command)]
      (if state
        (.ExitCode state)
        1))))

(defn process-result [options command argv out-buffer err-buffer error]
  {:cmd argv
   :exit (process-exit-code command error)
   :out (buffer-value out-buffer (:out options))
   :err (buffer-value err-buffer (:err options))})

(defn continue? [option result]
  (cond
    (true? option) true
    (fn? option) (boolean (option result))
    :else (zero? (:exit result))))

(defn shell [& arguments]
  (capabilities/require! :process :spawn)
  (let [previous (when (and (map? (first arguments))
                            (contains? (first arguments) :exit))
                   (first arguments))
        arguments (if previous (next arguments) arguments)
        options (if (map? (first arguments))
                  (first arguments)
                  {})
        arguments (if (map? (first arguments))
                    (next arguments)
                    arguments)
        options (if (and previous
                         (not (contains? options :in))
                         (string? (:out previous)))
                  (assoc options :in (:out previous))
                  options)
        argv (command-arguments arguments)
        command (apply os:exec.Command argv)
        out-buffer (when (contains? #{:string :bytes}
                                    (:out options))
                     (new bytes.Buffer))
        err-buffer (when (contains? #{:string :bytes}
                                    (:err options))
                     (new bytes.Buffer))
        out-file
        (when (contains? #{:write :append} (:out options))
          (open-output-file (:out-file options)
                            (= :append (:out options))))
        err-file
        (when (contains? #{:write :append} (:err options))
          (open-output-file (:err-file options)
                            (= :append (:err options))))
        input (:in options)
        input-file
        (when (or (instance? File input)
                  (instance? Path input))
          (let [[stream error] (os.Open (str input))]
            (when error
              (throw
               (ex-info
                (str "Could not open process input " input
                     ": " (fmt.Sprint error))
                {:gobb/process :redirect
                 :path (str input)})))
            stream))
        stdout
        (cond
          out-buffer out-buffer
          out-file out-file
          (= :inherit (:out options)) os.Stdout
          (= :discard (:out options)) io.Discard
          :else os.Stdout)
        stderr
        (cond
          (= :out (:err options)) stdout
          err-buffer err-buffer
          err-file err-file
          (= :inherit (:err options)) os.Stderr
          (= :discard (:err options)) io.Discard
          :else os.Stderr)]
    (when-let [directory (:dir options)]
      (set! (.Dir command) (str directory)))
    (set! (.Env command) (environment-entries options))
    (set! (.Stdin command)
          (cond
            input-file input-file
            (= :inherit input) os.Stdin
            (string? input) (strings.NewReader input)
            input input
            :else nil))
    (set! (.Stdout command) stdout)
    (set! (.Stderr command) stderr)
    (let [error (.Run command)
          result (process-result options command argv
                                 out-buffer err-buffer error)]
      (when input-file (.Close input-file))
      (when out-file (.Close out-file))
      (when err-file (.Close err-file))
      (if (continue? (:continue options) result)
        result
        (if-let [error-fn (:error-fn options)]
          (error-fn {:proc result
                     :task *task*
                     :babashka/exit (:exit result)})
          (throw
           (ex-info
            (str "Error while executing task: " (:name *task*))
            {:proc result
             :task *task*
             :babashka/exit (:exit result)})))))))

(defn clojure [& arguments]
  (let [executable (or (not-empty (os.Getenv "GOBB_EXECUTABLE"))
                       (first os.Args))]
    (apply shell executable arguments)))

(defn run
  ([task-name]
   (run task-name nil))
  ([task-name options]
   (when-not *execution*
     (throw
      (ex-info "babashka.tasks/run is only available while running tasks"
               {:babashka/exit 1})))
   ((:runner *execution*) task-name
    (merge {:parallel (:parallel *task*)}
           options))))

(defn require-source [requires]
  (when (seq requires)
    (pr-str
     (cons 'require
           (map #(list 'quote %) requires)))))

(defn evaluate! [source]
  (host/evaluate-source
   (str "(in-ns '" (:namespace *execution*) ")\n" source)
   {:args (:args *execution*)
    :file (or (System/getProperty "babashka.config")
              host/no-source-path)}))

(defn install-task-bindings! []
  (evaluate!
   "(def shell babashka.tasks/shell)
    (def clojure babashka.tasks/clojure)
    (def current-task babashka.tasks/current-task)
    (def current-state babashka.tasks/current-state)
    (def run babashka.tasks/run)"))

(defn initialize-tasks! []
  (when (compare-and-set! (:initialized *execution*) false true)
    (evaluate! "(refer 'clojure.core)")
    (install-task-bindings!)
    (when-let [source (require-source (:requires (task-config)))]
      (evaluate! source))
    (when-let [init (:init (task-config))]
      (evaluate! (pr-str init)))))

(defn task-map [task-name definition]
  (let [tasks (task-config)
        definition-map (if (map? definition) definition {})
        enter (if (contains? definition-map :enter)
                (:enter definition-map)
                (:enter tasks))
        leave (if (contains? definition-map :leave)
                (:leave definition-map)
                (:leave tasks))]
    (cond-> (merge definition-map {:name task-name})
      enter (assoc :enter enter)
      leave (assoc :leave leave)
      (:parallel *execution*) (assoc :parallel true))))

(defn task-body [definition]
  (let [body (if (map? definition)
               (:task definition)
               definition)]
    (cond
      (qualified-symbol? body)
      (list 'apply body '*command-line-args*)

      (nil? body) nil
      :else body)))

(defn task-source [task-name definition]
  (let [task (task-map task-name definition)
        body (task-body definition)
        enter (:enter task)
        leave (:leave task)
        body-source (pr-str body)]
    (str
     "(binding [babashka.tasks/*task* '" (pr-str task) "]\n"
     (when enter (str "  " (pr-str enter) "\n"))
     "  (let [result " body-source "]\n"
     (when leave
       (str "    (binding [babashka.tasks/*task*\n"
            "              (assoc babashka.tasks/*task* :result result)]\n"
            "      " (pr-str leave) ")\n"))
     "    result))")))

(defn task-dependencies [task-name]
  (let [definition (task-definition task-name)]
    (if (and (map? definition) (seq (:depends definition)))
      (mapv #(symbol (str %)) (:depends definition))
      [])))

(defn validate-graph! [task-name]
  (letfn [(visit [name visiting visited]
            (when (contains? visiting name)
              (throw
               (ex-info (str "Cyclic task: " name)
                        {:babashka/exit 1
                         :task name})))
            (when-not (contains? visited name)
              (when-not (task-defined? name)
                (throw
                 (ex-info (str "No such task: " name)
                          {:babashka/exit 1
                           :task name})))
              (reduce
               (fn [seen dependency]
                 (visit dependency (conj visiting name) seen))
               (conj visited name)
               (task-dependencies name))))]
    (visit task-name #{} #{})))

(defn target-order
  ([task-name]
   (target-order task-name (atom #{}) []))
  ([task-name processed order]
   (if (contains? @processed task-name)
     order
     (let [order (reduce
                  (fn [result dependency]
                    (target-order dependency processed result))
                  order
                  (task-dependencies task-name))]
       (swap! processed conj task-name)
       (conj order task-name)))))

(defn target-levels [task-name]
  (let [nodes (set (target-order task-name))]
    (loop [remaining nodes
           completed #{}
           levels []]
      (if (empty? remaining)
        levels
        (let [ready (->> (task-names)
                         (filter remaining)
                         (filter #(every? completed
                                          (task-dependencies %)))
                         vec)]
          (when (empty? ready)
            (throw
             (ex-info (str "Cyclic task: " task-name)
                      {:babashka/exit 1
                       :task task-name})))
          (recur (reduce disj remaining ready)
                 (into completed ready)
                 (conj levels ready)))))))

(defn prepare-task! [task-name]
  (when-let [source
             (require-source
              (:requires (task-definition task-name)))]
    (evaluate! source)))

(defn intern-result! [task-name result]
  (intern (the-ns (:namespace *execution*)) task-name result)
  (swap! (:results *execution*) assoc task-name result)
  result)

(defn execute-one! [task-name]
  (if (contains? @(:results *execution*) task-name)
    (get @(:results *execution*) task-name)
    (do
      (System/setProperty "babashka.task" (str task-name))
      (let [result (evaluate!
                    (task-source task-name
                                 (task-definition task-name)))]
        (intern-result! task-name result)))))

(defn execute-sequential! [task-name]
  (doseq [name (target-order task-name)]
    (prepare-task! name)
    (execute-one! name))
  (get @(:results *execution*) task-name))

(defn execute-parallel! [task-name]
  (doseq [level (target-levels task-name)]
    (doseq [name level]
      (prepare-task! name))
    (let [jobs (mapv (fn [name]
                       [name (future (execute-one! name))])
                     level)]
      (doseq [[_ job] jobs]
        @job)))
  (get @(:results *execution*) task-name))

(declare execute-task!)

(defn run-in-execution! [task-name options]
  (let [task-name (symbol (str task-name))]
    (validate-graph! task-name)
    (if (:parallel options)
      (execute-parallel! task-name)
      (execute-sequential! task-name))))

(defn execute-task!
  ([task-name args]
   (execute-task! task-name args {}))
  ([task-name args options]
   (let [task-name (symbol (str task-name))
         context {:namespace 'user
                  :initialized (atom false)
                  :results (atom {})
                  :args args
                  :parallel (boolean (:parallel options))}]
     (host/set-command-line-args! args)
     (binding [*execution*
               (assoc context
                      :runner
                      (fn [nested-task nested-options]
                        (run-in-execution!
                         nested-task nested-options)))]
       (initialize-tasks!)
       (let [result (run-in-execution! task-name options)]
         (when (:print-result options)
           (prn result))
         result)))))
