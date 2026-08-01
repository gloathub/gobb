(ns babashka.classpath
  (:refer-clojure :exclude [add-classpath])
  (:require [gobb.host :as host]))

(def path-separator (System/getProperty "path.separator"))

(defn split-classpath
  "Returns the classpath as a vector of platform-specific path entries."
  [classpath]
  (vec (when classpath (path:filepath.SplitList classpath))))

(defn get-classpath
  "Returns the classpath configured for the current Gobb process."
  []
  (let [classpath (System/getProperty "java.class.path")]
    (when (seq classpath) classpath)))

(defn add-classpath
  "Adds a platform-separated path string to Gobb's runtime load path."
  [extra-classpath]
  (doseq [entry (split-classpath extra-classpath)]
    (host/add-load-path! entry))
  (let [classpath (get-classpath)]
    (System/setProperty
     "java.class.path"
     (if (seq classpath)
       (str classpath path-separator extra-classpath)
       extra-classpath)))
  nil)
