(require '[cheshire.core :as json])

(defn canonical [value]
  (cond
    (map? value)
    (into (sorted-map)
          (map (fn [[key item]]
                 [key (canonical item)]))
          value)
    (vector? value) (mapv canonical value)
    (sequential? value) (map canonical value)
    :else value))

(println
 (pr-str
  (canonical
   {:parse
   (json/parse-string
    "{\"name\":\"gobb\",\"count\":3,\"ok\":true,\"items\":[1,2,null]}")
   :keyword-keys
   (json/parse-string "{\"outer\":{\"inner\":42}}" true)
   :key-fn
   (json/parse-string "{\"mixedCase\":1}" clojure.string/upper-case)
   :roundtrip
   (json/parse-string
    (json/generate-string
     {:name "gobb"
      :enabled true
      :items [1 "two" nil]})
    true)
   :stream
   (json/parse-stream
    (java.io.StringReader. "{\"streamed\":[3,4]}"))
   :stream-output
   (let [output (java.io.StringWriter.)]
     (json/generate-stream ["a" 2 false] output)
     (str output))
   :sequence
   (vec
    (json/parsed-seq
     (java.io.StringReader. "{\"a\":1} [2,3] true")))})))
