(ns gobb.web-repl
  (:require [gobb.repl :as repl]))

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
            (repl/handle-command form request-prompt request-exit)))))))

(defn -main [& _]
  (repl/start read-form))
