(ns clojure.java.io
  (:require [gobb.host :as host]))

(defn resource [name]
  (host/resource-path name))
