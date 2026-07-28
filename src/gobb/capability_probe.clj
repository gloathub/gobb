(ns gobb.capability-probe
  (:require [gobb.capabilities :as capabilities]
            [gobb.capability-matrix :as capability-matrix]))

(defn structured-error [capability operation]
  (try
    (capabilities/require! capability operation)
    nil
    (catch Exception error
      (select-keys
       (ex-data error)
       [:type :capability :target :platform :operation :status]))))

(defn statuses []
  (into (sorted-map)
        (for [capability (keys capability-matrix/matrix)]
          [capability (capabilities/status capability)])))

(defn native-adapters []
  (when (= :native (capabilities/target))
    (let [[directory directory-error]
          (os.MkdirTemp "" "gobb-capability-")]
      (when directory-error
        (throw directory-error))
      (let [file (path:filepath.Join directory "probe.txt")]
        (try
          (capabilities/write-bytes! file (.getBytes "capability"))
          {:filesystem (= "capability"
                          (go/string
                           (capabilities/read-bytes file)))
           :process (= {:output "capability" :error nil}
                       (capabilities/run-command
                        ["sh" "-c" "printf capability"]))
           :working-directory
           (= (capabilities/working-directory)
              (System/getProperty "user.dir"))}
          (finally
            (os.RemoveAll directory)))))))

(defn -main [& _]
  (prn
   {:target (capabilities/target)
    :platform (capabilities/platform)
    :statuses (statuses)
    :environment (= "visible"
                    (capabilities/environment
                     "GOBB_CAPABILITY_ENV"))
    :clock (pos? (capabilities/now-ms))
    :random (= 16 (count (capabilities/random-bytes 16)))
    :jvm-error (structured-error :jvm-bytecode :load-class)
    :process-error
    (when-not (= :native (capabilities/target))
      (structured-error :process :spawn))
    :filesystem-error
    (when (= :browser (capabilities/target))
      (structured-error :filesystem :read))
    :native-adapters (native-adapters)}))
