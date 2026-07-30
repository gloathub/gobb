(require '[clj-yaml.core :as yaml])

(def yaml-source
  (str "name: gobb\n"
       "enabled: true\n"
       "count: 3\n"
       "items:\n"
       "  - one\n"
       "  - 2\n"))

(defn canonical [value]
  (cond
    (map? value)
    (into (sorted-map)
          (map (fn [[key item]]
                 [key (canonical item)]))
          value)
    (sequential? value) (mapv canonical value)
    :else value))

(println
 (pr-str
  (canonical
   {:parse (yaml/parse-string yaml-source)
   :string-keys (yaml/parse-string yaml-source :keywords false)
   :key-fn (yaml/parse-string
            "mixedCase: 42\n"
            :key-fn #(clojure.string/upper-case (:key %)))
   :documents (yaml/parse-string
               "---\na: 1\n---\nb: 2\n"
               :load-all true)
   :stream (yaml/parse-stream
            (java.io.StringReader. "streamed: [3, 4]\n"))
   :roundtrip
   (yaml/parse-string
   (yaml/generate-string
     {:name "gobb"
      :nested {:items [1 "two" nil]}}))})))
