(ns gobb.capabilities
  (:require [gobb.capability-matrix :as capability-matrix]))

(defn target []
  (case runtime.GOOS
    "js" :browser
    "wasip1" :wasi
    :native))

(defn platform []
  (str runtime.GOOS "/" runtime.GOARCH))

(defn capability [capability-id]
  (or (get capability-matrix/matrix capability-id)
      (throw
       (ex-info
        (str "Unknown Gobb capability: " capability-id)
        {:type :gobb/unknown-capability
         :capability capability-id
         :target (target)
         :platform (platform)}))))

(defn disposition [capability-id]
  (get-in (capability capability-id) [:targets (target)]))

(defn status [capability-id]
  (:status (disposition capability-id)))

(defn available? [capability-id]
  (contains? #{:available :limited} (status capability-id)))

(defn unsupported-data
  ([capability-id operation]
   (unsupported-data capability-id operation
                     (disposition capability-id)))
  ([capability-id operation disposition]
   (cond->
    {:type :gobb/unsupported-capability
     :capability capability-id
     :target (target)
     :platform (platform)
     :operation operation
     :status (:status disposition)}
     (:rationale disposition)
     (assoc :rationale (:rationale disposition)))))

(defn unsupported! [capability-id operation]
  (throw
   (ex-info
    (str "Gobb capability " capability-id
         " does not support " operation
         " on " (platform))
    (unsupported-data capability-id operation))))

(defn require! [capability-id operation]
  (let [disposition (disposition capability-id)]
    (when (= :unavailable (:status disposition))
      (unsupported! capability-id operation))
    disposition))

(defn environment [name]
  (require! :environment :get)
  (os.Getenv name))

(defn now-ms []
  (require! :clock :now)
  (.UnixMilli (time.Now)))

(defn random-bytes [size]
  (require! :random :bytes)
  (when (neg? size)
    (throw
     (ex-info "Random byte count must not be negative"
              {:type :gobb/invalid-capability-argument
               :capability :random
               :operation :bytes
               :size size})))
  (let [buffer (go/make (go/slice-of go/byte) size)
        [count error] (crypto:rand.Read buffer)]
    (when error
      (throw
       (ex-info
        (str "Could not read secure random bytes: "
             (fmt.Sprint error))
        {:type :gobb/capability-error
         :capability :random
         :operation :bytes
         :platform (platform)})))
    (when-not (= size count)
      (throw
       (ex-info
        (str "Short secure random read: " count " of " size)
        {:type :gobb/capability-error
         :capability :random
         :operation :bytes
         :platform (platform)
         :expected size
         :actual count})))
    buffer))

(defn read-bytes [file]
  (require! :filesystem :read)
  (let [[content error] (os.ReadFile file)]
    (when error
      (throw
       (ex-info
        (str "Could not read " file ": " (fmt.Sprint error))
        {:type :gobb/capability-error
         :capability :filesystem
         :operation :read
         :platform (platform)
         :file file})))
    content))

(defn write-bytes! [file content]
  (require! :filesystem :write)
  (let [error (os.WriteFile file content 0644)]
    (when error
      (throw
       (ex-info
        (str "Could not write " file ": " (fmt.Sprint error))
        {:type :gobb/capability-error
         :capability :filesystem
         :operation :write
         :platform (platform)
         :file file})))
    file))

(defn working-directory []
  (require! :working-directory :get)
  (let [[directory error] (os.Getwd)]
    (when error
      (throw
       (ex-info
        (str "Could not read the working directory: "
             (fmt.Sprint error))
        {:type :gobb/capability-error
         :capability :working-directory
         :operation :get
         :platform (platform)})))
    directory))

(defn run-command [argv]
  (require! :process :spawn)
  (when-not (seq argv)
    (throw
     (ex-info "Process command must not be empty"
              {:type :gobb/invalid-capability-argument
               :capability :process
               :operation :spawn})))
  (let [command (apply os:exec.Command argv)
        [output error] (.CombinedOutput command)]
    {:output (go/string output)
     :error (when error (fmt.Sprint error))}))
