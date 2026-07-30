(require '[cognitect.transit :as transit])

(def output (java.io.ByteArrayOutputStream. 4096))
(def writer (transit/writer output :json))

(transit/write writer "foo")
(transit/write writer {:a [1 2]})
(transit/write writer
               {:keyword :gobb/value
                :symbol 'gobb/value
                :set #{1 2}
                :list '(a b)})

(def wire (.toString output))
(def input (java.io.ByteArrayInputStream. (.toByteArray output)))
(def reader (transit/reader input :json))

(println
 (pr-str
  {:wire-prefix
   (.startsWith wire "[\"~#'\",\"foo\"]")
   :first (transit/read reader)
   :second (transit/read reader)
   :third (transit/read reader)}))
