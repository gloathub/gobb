(ns gobb.host)

(def no-source-path "NO_SOURCE_PATH")
(def repl-source-path "<repl>")

(defn initialize! []
  ;; Glojure initializes *in* and *out* at bootstrap, but its generated
  ;; clojure.core currently leaves *err* nil. Own all three roots here so the
  ;; Gobb execution host has one explicit standard-stream contract.
  (alter-var-root #'*in* (constantly os.Stdin))
  (alter-var-root #'*out* (constantly os.Stdout))
  (alter-var-root #'*err* (constantly os.Stderr)))

(defn set-command-line-args! [args]
  (alter-var-root #'*command-line-args* (constantly (seq args))))

(defn set-file! [file]
  (alter-var-root #'*file* (constantly file)))

(defn evaluate-source
  [source {:keys [args file print-result?]
           :or {args ()
                file no-source-path
                print-result? false}}]
  (set-command-line-args! args)
  ;; A single enclosing do lets the runtime reader accept any number of forms
  ;; while keeping them in the same Glojure environment.
  (binding [*file* file]
    (let [form (read-string (str "(do\n" source "\n)"))
          result (eval form)]
      (when (and print-result? (some? result))
        (prn result))
      result)))
