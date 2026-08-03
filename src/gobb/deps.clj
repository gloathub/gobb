(ns gobb.deps
  "Gobb's runtime dependency facade."
  (:require [gobb.project :as project]))

(defonce ^:private basis (atom {}))

(defn current-basis [] @basis)

(defn add-libs
  ([libs] (add-libs libs nil))
  ([libs opts]
   (let [deps-map (cond-> {:deps libs}
                    (:local-repo opts)
                    (assoc :mvn/local-repo (:local-repo opts))

                    (:repos opts)
                    (assoc :mvn/repos (:repos opts)))]
     (project/add-deps! deps-map)
     (swap! basis merge libs)
     nil)))

(defn add-lib
  ([lib coordinate] (add-lib lib coordinate nil))
  ([lib coordinate opts]
   (add-libs {lib coordinate} opts)))

(defn add-deps
  ([deps-map] (add-deps deps-map nil))
  ([deps-map opts]
   (project/add-deps!
    (cond-> deps-map
      (:local-repo opts)
      (assoc :mvn/local-repo (:local-repo opts))

      (:repos opts)
      (assoc :mvn/repos (:repos opts))))
   (swap! basis merge (or (:deps deps-map) {}))
   nil))

(defn sync-deps
  ([] (sync-deps "deps.edn" nil))
  ([path] (sync-deps path nil))
  ([path opts]
   (add-deps (read-string (slurp path)) opts)))
