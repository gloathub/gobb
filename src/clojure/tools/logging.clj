(ns clojure.tools.logging
  (:require [clojure.tools.logging.impl :as impl]
            [gobb.logging :as logging]))

(def ^:dynamic *logger-factory* :gobb)

(defn log* [logger level throwable message]
  (impl/write! logger level throwable message))

(defmacro log
  ([level message]
   `(log ~level nil ~message))
  ([level throwable message]
   `(log *logger-factory* ~(str *ns*) ~level ~throwable ~message))
  ([logger-ns level throwable message]
   `(log *logger-factory* ~logger-ns ~level ~throwable ~message))
  ([logger-factory logger-ns level throwable message]
   `(let [logger# (impl/get-logger ~logger-factory ~logger-ns)]
      (when (impl/enabled? logger# ~level)
        (log* logger# ~level ~throwable ~message)))))

(defmacro enabled?
  ([level]
   `(logging/enabled? ~level))
  ([level _logger-ns]
   `(logging/enabled? ~level)))

(defmacro logp [level & values]
  `(log ~level (logging/message [~@values])))

(defmacro logf [level format-string & values]
  `(log ~level (format ~format-string ~@values)))

(defmacro trace [& values] `(logp :trace ~@values))
(defmacro debug [& values] `(logp :debug ~@values))
(defmacro info [& values] `(logp :info ~@values))
(defmacro warn [& values] `(logp :warn ~@values))
(defmacro error [& values] `(logp :error ~@values))
(defmacro fatal [& values] `(logp :fatal ~@values))

(defmacro tracef [format-string & values]
  `(logf :trace ~format-string ~@values))
(defmacro debugf [format-string & values]
  `(logf :debug ~format-string ~@values))
(defmacro infof [format-string & values]
  `(logf :info ~format-string ~@values))
(defmacro warnf [format-string & values]
  `(logf :warn ~format-string ~@values))
(defmacro errorf [format-string & values]
  `(logf :error ~format-string ~@values))
(defmacro fatalf [format-string & values]
  `(logf :fatal ~format-string ~@values))

(defmacro spy
  ([expression] `(spy :debug ~expression))
  ([level expression]
   `(let [value# ~expression]
      (log ~level (str '~expression " => " value#))
      value#)))

(defmacro spyf
  ([format-string expression]
   `(spyf :debug ~format-string ~expression))
  ([level format-string expression]
   `(let [value# ~expression]
      (log ~level (format ~format-string value#))
      value#)))
