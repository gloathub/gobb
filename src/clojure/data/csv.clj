(ns clojure.data.csv
  (:require [clojure.java.io :as io]))

(defn fail! [operation error]
  (throw
   (ex-info
    (str "CSV " operation " failed: " (fmt.Sprint error))
    {:gobb/csv operation})))

(defn csv-reader [input separator]
  (let [reader (encoding:csv.NewReader
                (if (string? input)
                  ;; java.io readers recognize CR, LF, and CRLF as record
                  ;; separators. Go's encoding/csv accepts LF/CRLF, so
                  ;; normalize lone CR without doubling CRLF records.
                  (strings.NewReader
                   (-> input
                       (strings.ReplaceAll "\r\n" "\n")
                       (strings.ReplaceAll "\r" "\n")))
                  (io/reader input)))]
    (set! (.Comma reader) (go/rune separator))
    reader))

(defn read-csv
  "Reads CSV data from a string or reader into a sequence of vectors."
  [input & options]
  (let [{:keys [separator quote]
         :or {separator \,
              quote \"}} options]
    (when-not (= quote \")
      (throw
       (ex-info
        "Gobb clojure.data.csv currently supports only the default quote character"
        {:quote quote})))
    (let [[records error] (.ReadAll (csv-reader input separator))]
      (when error
        (let [message (fmt.Sprint error)]
          (if (strings.Contains message "extraneous or missing")
            (throw (errors.New message))
            (fail! :read error))))
      (seq (mapv vec records)))))

(defn csv-writer [output separator]
  (let [writer (encoding:csv.NewWriter output)]
    (set! (.Comma writer) (go/rune separator))
    writer))

(defn newline-value [newline]
  (case newline
    :lf false
    :cr+lf true
    (throw
     (ex-info
      (str "Unsupported CSV newline: " newline)
      {:newline newline}))))

(defn write-csv
  "Writes records to a writer in CSV format."
  [output data & options]
  (let [{:keys [separator quote quote? newline]
         :or {separator \,
              quote \"
              newline :lf}} options]
    (when-not (= quote \")
      (throw
       (ex-info
        "Gobb clojure.data.csv currently supports only the default quote character"
        {:quote quote})))
    (when quote?
      (throw
       (ex-info
        "Gobb clojure.data.csv does not yet support a custom quote? predicate"
        {:quote? quote?})))
    (let [writer (csv-writer output separator)]
      (set! (.UseCRLF writer) (newline-value newline))
      (doseq [record data]
        (let [values (apply go/append
                            (go/make (go/slice-of go/string) 0)
                            (map str record))
              error (.Write writer values)]
          (when error
            (fail! :write error))))
      (.Flush writer)
      (when-let [error (.Error writer)]
        (fail! :write error))
      nil)))
