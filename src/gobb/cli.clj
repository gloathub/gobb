(ns gobb.cli
  (:require [babashka.impl.exceptions]
            [gobb.version]))

(def usage
  "Usage: gobb -e EXPR [ARGS...]\n       gobb FILE [ARGS...]\n       SOURCE | gobb")

(defn fail! [message]
  (fmt.Fprintln os.Stderr (str "gobb: " message))
  (fmt.Fprintln os.Stderr usage)
  (os.Exit 1))

(defn set-command-line-args! [args]
  (alter-var-root #'*command-line-args* (constantly (seq args))))

(defn evaluate-source [source print-result?]
  ;; A single enclosing do lets the runtime reader accept any number of forms
  ;; while keeping them in the same Glojure environment.
  (let [form (read-string (str "(do\n" source "\n)"))
        result (eval form)]
    (when (and print-result? (some? result))
      (prn result))
    result))

(defn evaluate-expression [expression args]
  (set-command-line-args! args)
  (evaluate-source expression true))

(defn evaluate-file [file args]
  (set-command-line-args! args)
  (evaluate-source (slurp file) false))

(defn -main [& argv]
  (cond
    (empty? argv)
    (evaluate-source (slurp *in*) true)

    (= "-e" (first argv))
    (if-let [expression (second argv)]
      (evaluate-expression expression (drop 2 argv))
      (fail! "-e requires an expression"))

    (or (= "-h" (first argv))
        (= "--help" (first argv)))
    (println usage)

    (= "--version" (first argv))
    (println (str "gobb v" gobb.version/version))

    (.startsWith (str (first argv)) "-")
    (fail! (str "unknown option: " (first argv)))

    :else
    (evaluate-file (first argv) (rest argv))))
