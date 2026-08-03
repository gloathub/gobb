(ns clojure.java.shell
  (:require [babashka.process :as process]))

(def ^:dynamic *sh-dir* nil)
(def ^:dynamic *sh-env* nil)

(defmacro with-sh-dir [directory & body]
  `(binding [*sh-dir* ~directory]
     ~@body))

(defmacro with-sh-env [environment & body]
  `(binding [*sh-env* ~environment]
     ~@body))

(defn sh
  "Runs a command and returns a map containing :exit, :out, and :err.

  The command is a sequence of strings followed by optional :in, :env, and
  :dir keyword arguments, matching clojure.java.shell/sh."
  [& arguments]
  (let [[command option-arguments] (split-with string? arguments)
        options (apply hash-map option-arguments)
        options (cond-> options
                  (and *sh-dir* (not (contains? options :dir)))
                  (assoc :dir *sh-dir*)
                  (and *sh-env* (not (contains? options :env)))
                  (assoc :env *sh-env*))]
    (select-keys
     (apply process/sh options command)
     [:exit :out :err])))
