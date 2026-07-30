(ns clojure.tools.logging.impl
  (:require [gobb.logging :as logging]))

(defn get-logger [_factory logger-ns]
  {:logger-ns (str logger-ns)})

(defn enabled? [_logger level]
  (logging/enabled? level))

(defn write! [_logger level throwable message]
  (logging/emit! level throwable message))

(defn find-factory []
  :gobb)
