(ns gobb.logging
  (:require [clojure.string :as str]))

(def levels
  {:trace 0
   :debug 1
   :info 2
   :warn 3
   :error 4
   :fatal 5
   :report 6})

(def ^:dynamic *min-level* :trace)

(defn enabled? [level]
  (<= (get levels *min-level* 0)
      (get levels level 0)))

(defn message [values]
  (str/join " " (map str values)))

(defn emit! [level throwable text]
  (when (enabled? level)
    (binding [*out* *err*]
      (println (str "[" (name level) "] " text))
      (when throwable
        (println (str throwable)))))
  nil)
