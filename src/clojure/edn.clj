(ns clojure.edn
  (:refer-clojure :exclude [read read-string])
  (:require [gobb.host :as host]))

(defn read
  ([stream]
   (host/read* stream))
  ([options stream]
   (host/read* options stream)))

(defn read-string
  ([string]
   (host/read* (StringReader. string)))
  ([options string]
   (host/read* options (StringReader. string))))
