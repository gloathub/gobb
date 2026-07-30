(ns clojure.edn
  (:refer-clojure :exclude [read read-string]))

(defn read
  ([stream]
   (clojure.core/read stream))
  ([options stream]
   (clojure.core/read options stream)))

(defn read-string
  ([string]
   (clojure.core/read-string string))
  ([options string]
   (clojure.core/read-string options string)))
