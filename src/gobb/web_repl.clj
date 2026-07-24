(ns gobb.web-repl
  (:require [gobb.bb-repl :as bb-repl]
            [gobb.version]))

(def help-text
  "Gobb browser REPL

Enter a Clojure form and press Enter.
  :repl/help   Show this help
  :repl/exit   Exit the REPL

Evaluation uses the full Glojure runtime. The read-eval-print loop is adapted
from Babashka's babashka.impl.clojure.main/repl.")

(defn read-form [request-prompt request-exit]
  ;; Scan a whitespace-free URL-encoded token. The browser terminal performs
  ;; the encoding, allowing multiline forms to cross the line-oriented fd.
  (let [value (reflect.New (reflect.TypeOf ""))
        [count scan-error] (fmt.Scanln (.Interface value))]
    (if (zero? count)
      request-exit
      (let [encoded (.Interface (.Elem value))
            [source decode-error] (net:url.QueryUnescape encoded)]
        (if decode-error
          (throw decode-error)
          (let [form (read-string source)]
            (case form
              :repl/help (do (println help-text) request-prompt)
              :repl/exit request-exit
              :repl/quit request-exit
              form)))))))

(defn prompt []
  (print (str (ns-name *ns*) "=> ")))

(defn caught [error]
  (println (str "Error: " error)))

(defn -main [& _]
  (bb-repl/repl
   :init #(do
            (println (str "Gobb v" gobb.version/version))
            (println "Babashka REPL loop, powered by Gloat and Glojure.")
            (println "Type :repl/help for help.")
            (in-ns 'user)
            (refer 'clojure.core))
   :need-prompt (constantly true)
   :prompt prompt
   :flush flush
   :read read-form
   :eval eval
   :print prn
   :caught caught))
