(ns gobb.repl
  (:require [gobb.bb-repl :as bb-repl]
            [gobb.host :as host]
            [gobb.version]))

(def help-text
  "Gobb REPL

Enter a Clojure form and press Enter.
  :repl/help   Show this help
  :repl/exit   Exit the REPL

Evaluation uses the full Glojure runtime. The read-eval-print loop is adapted
from Babashka's babashka.impl.clojure.main/repl.")

(defn handle-command [form request-prompt request-exit]
  (case form
    :repl/help (do (println help-text) request-prompt)
    :repl/exit request-exit
    :repl/quit request-exit
    form))

(defn prompt []
  (print (str (ns-name *ns*) "=> ")))

(defn caught [error]
  (println (str "Error: " (fmt.Sprint error))))

(defn start [read-form]
  (host/initialize!)
  (host/set-file! host/repl-source-path)
  (System/setProperty
   "babashka.version" gobb.version/babashka-version)
  (when (nil? (System/getProperty "java.class.path"))
    (System/setProperty "java.class.path" ""))
  (bb-repl/repl
   :init #(do
            (println (str "Gobb v" gobb.version/version))
            (println (str "Babashka v" gobb.version/babashka-version))
            (println "Type :repl/help for help")
            (in-ns 'user)
            (refer 'clojure.core))
   :need-prompt (constantly true)
   :prompt prompt
   :flush flush
   :read read-form
   :eval eval
   :print prn
   :caught caught))
