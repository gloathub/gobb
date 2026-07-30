(ns babashka.pods
  (:require [gobb.capabilities :as capabilities]))

(defonce loaded-pods (atom {}))

(defn pod-command [pod-spec options]
  (cond
    (string? pod-spec)
    [pod-spec]

    (sequential? pod-spec)
    (mapv str pod-spec)

    (:path options)
    [(let [path (:path options)
           config (System/getProperty "babashka.config")]
       (if (or (path:filepath.IsAbs path)
               (empty? config))
         path
         (path:filepath.Join
          (path:filepath.Dir config) path)))]

    (:version options)
    (throw
     (ex-info
      "Registry pod downloads are not implemented by Gobb yet"
      {:type :gobb/unsupported-pod-coordinate
       :pod pod-spec
       :version (:version options)}))

    :else
    (throw
     (ex-info
      "A pod must be an executable, command vector, or :path coordinate"
      {:type :gobb/invalid-pod-coordinate
       :pod pod-spec
       :options options}))))

(defn load-pod* [pod-spec options]
  (capabilities/require! :process {:operation :load-pod})
  (let [command (pod-command pod-spec options)
        [client error]
        (github.com:glojurelang:glojure:pkg:podclient.StartCommand
         (vec command))]
    (when error
      (throw error))
    (let [id (.ID client)]
      (swap! loaded-pods assoc id client)
      {:pod/id id})))

(defn load-pod
  ([pod-spec]
   (load-pod* pod-spec {}))
  ([pod-spec version-or-options]
   (load-pod*
    pod-spec
    (if (string? version-or-options)
      {:version version-or-options}
      (or version-or-options {}))))
  ([pod-spec version options]
   (load-pod* pod-spec
              (assoc (or options {}) :version version))))

(defn pod-id [pod]
  (if (map? pod) (:pod/id pod) pod))

(defn invoke
  ([pod variable arguments]
   (invoke pod variable arguments nil))
  ([pod variable arguments _options]
   (let [id (pod-id pod)
         client (get @loaded-pods id)]
     (when-not client
       (throw
        (ex-info (str "Pod is not loaded: " id)
                 {:type :gobb/pod-not-loaded
                  :pod/id id})))
     (let [[value error]
           (.Invoke client (str variable) (vec arguments))]
       (when error
         (throw error))
       value))))

(defn unload-pod
  ([pod] (unload-pod pod nil))
  ([pod _options]
   (let [id (pod-id pod)]
     (when-let [client (get @loaded-pods id)]
       (swap! loaded-pods dissoc id)
       (.Close client))
     nil)))

(defn load-configured-pods! [pods]
  (doseq [[pod-spec options] pods]
    (load-pod pod-spec options))
  nil)

(defn unsupported-handler! [operation]
  (throw
   (ex-info
    (str "babashka.pods/" operation
         " is only available for Transit pods")
    {:type :gobb/unsupported-pod-operation
     :operation operation})))

(defn add-transit-read-handler! [& _]
  (unsupported-handler! "add-transit-read-handler!"))

(defn add-transit-write-handler! [& _]
  (unsupported-handler! "add-transit-write-handler!"))

(defn set-default-transit-write-handler! [& _]
  (unsupported-handler! "set-default-transit-write-handler!"))
