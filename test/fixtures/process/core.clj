(require '[babashka.process :as process]
         '[clojure.string :as string])

(defn result [value]
  (select-keys value [:cmd :exit :out :err]))

(let [directory (first *command-line-args*)
      callbacks (atom [])
      output-file (str directory "/process-output.txt")
      simple
      (process/sh "printf" "hello")
      asynchronous
      @(process/process
        {:out :string :err :string}
        "printf" "world")
      environment
      (process/sh
       {:extra-env {"GOBB_PROCESS_VALUE" "visible"}}
       "sh" "-c" "printf \"$GOBB_PROCESS_VALUE\"")
      working-directory
      (process/sh {:dir directory} "pwd")
      first-process
      (process/process "printf" "pipeline")
      second-process
      (process/process
       first-process
       {:out :string :err :string}
       "tr" "a-z" "A-Z")
      pipeline-result @second-process
      builder-result
      @(process/start
        (process/pb
         {:out :string :err :string}
         "printf" "builder"))
      macro-result
      @(process/$
        {:out :string :err :string}
        printf "macro")
      callback-result
      @(process/process
        {:out :string
         :err :string
         :pre-start-fn
         #(swap! callbacks conj [:start (:cmd %)])
         :exit-fn
         #(swap! callbacks conj [:exit (:exit %)])}
        "printf" "callbacks")
      bytes-result
      @(process/process
        {:in "byte input"
         :out :bytes
         :err :string}
        "cat")
      _ @(process/process
          {:out output-file :err :string}
          "printf" "written")
      _ @(process/process
          {:out :append
           :out-file output-file
           :err :string}
          "printf" "-appended")
      merged-result
      @(process/process
        {:out :string :err :out}
        "sh" "-c" "printf output; printf error >&2")
      failure
      (process/sh
       {:continue true}
       "sh" "-c" "printf failure >&2; exit 7")]
  (prn
   {:tokenize (process/tokenize "one 'two words' \"three words\"")
    :simple (result simple)
    :async (result asynchronous)
    :environment (:out environment)
    :directory (= directory
                  (string/trim (:out working-directory)))
    :pipeline [(count (process/pipeline second-process))
               (:out pipeline-result)]
    :builder (:out builder-result)
    :macro (:out macro-result)
    :callbacks [(:out callback-result) @callbacks]
    :redirection [(vec (:out bytes-result))
                  (slurp output-file)
                  (:out merged-result)
                  (string? (:err merged-result))]
    :failure [(:exit failure) (:err failure)]}))
