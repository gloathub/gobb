(ns clojure.tools.logging.readable
  (:require [clojure.tools.logging :as logging]))

(defmacro trace [& values] `(logging/trace ~@values))
(defmacro debug [& values] `(logging/debug ~@values))
(defmacro info [& values] `(logging/info ~@values))
(defmacro warn [& values] `(logging/warn ~@values))
(defmacro error [& values] `(logging/error ~@values))
(defmacro fatal [& values] `(logging/fatal ~@values))
