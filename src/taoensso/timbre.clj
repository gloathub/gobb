(ns taoensso.timbre
  (:require [gobb.logging :as logging]))

(def default-config
  {:min-level :trace})

(def ^:dynamic *config* default-config)

(defn enabled?
  ([level]
   (enabled? level nil))
  ([level _namespace]
   (binding [logging/*min-level* (:min-level *config* :trace)]
     (logging/enabled? level))))

(defmacro with-level [level & body]
  `(binding [*config* (assoc *config* :min-level ~level)]
     ~@body))

(defmacro with-config [config & body]
  `(binding [*config* ~config]
     ~@body))

(defmacro log! [level & values]
  `(binding [logging/*min-level* (:min-level *config* :trace)]
     (logging/emit! ~level nil (logging/message [~@values]))))

(defmacro trace [& values] `(log! :trace ~@values))
(defmacro debug [& values] `(log! :debug ~@values))
(defmacro info [& values] `(log! :info ~@values))
(defmacro warn [& values] `(log! :warn ~@values))
(defmacro error [& values] `(log! :error ~@values))
(defmacro fatal [& values] `(log! :fatal ~@values))
(defmacro report [& values] `(log! :report ~@values))

(defmacro tracef [format-string & values]
  `(trace (format ~format-string ~@values)))
(defmacro debugf [format-string & values]
  `(debug (format ~format-string ~@values)))
(defmacro infof [format-string & values]
  `(info (format ~format-string ~@values)))
(defmacro warnf [format-string & values]
  `(warn (format ~format-string ~@values)))
(defmacro errorf [format-string & values]
  `(error (format ~format-string ~@values)))
(defmacro fatalf [format-string & values]
  `(fatal (format ~format-string ~@values)))

(defmacro spy
  ([expression] `(spy :debug ~expression))
  ([level expression]
   `(let [value# ~expression]
      (log! ~level (str '~expression " => " value#))
      value#)))
