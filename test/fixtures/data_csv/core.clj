(require '[clojure.data.csv :as csv])

(def csv-source
  (str "name,count,note\n"
       "alpha,1,plain\n"
       "beta,2,\"comma, quote \"\"inside\"\"\"\n"))

(println
 (pr-str
  {:read (vec (csv/read-csv csv-source))
   :semicolon
   (vec (csv/read-csv "left;right\n1;2\n" :separator \;))
   :written
   (let [output (java.io.StringWriter.)]
     (csv/write-csv output
                    [["name" "value"]
                     ["quoted" "a,b"]
                     ["escaped" "say \"hello\""]])
     (str output))
   :crlf
   (let [output (java.io.StringWriter.)]
     (csv/write-csv output [["a" "b"] ["1" "2"]]
                    :newline :cr+lf)
     (str output))}))
