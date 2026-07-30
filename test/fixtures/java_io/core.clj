(require '[clojure.java.io :as io])

(let [root (first *command-line-args*)
      nested (io/file root "one" "two" "data.txt")
      made (io/make-parents nested)
      _ (with-open [output (io/output-stream nested)]
          (io/copy "first" output))
      _ (with-open [output (io/output-stream nested :append true)]
          (io/copy "-second" output))
      from-stream
      (with-open [input (io/input-stream nested)]
        (slurp input))
      from-reader
      (with-open [input (io/reader nested)]
        (slurp input))
      byte-input
      (with-open [input
                  (io/input-stream
                   (byte-array [98 121 116 101 115]))]
        (slurp input))
      writer-file (io/file root "writer.txt")
      _ (with-open [output (io/writer writer-file)]
          (io/copy "writer" output))
      string-output (java.io.StringWriter.)
      copied (io/copy "memory" string-output)
      resource
      (io/resource "classpath/fixture-resource.txt")
      relative (io/as-relative-path "one/two")
      deleted (io/delete-file writer-file)
      silent (io/delete-file writer-file true)]
  (prn
   {:file [(str (io/file "one" "two" "three"))
           (.getName (io/as-file nested))
           relative]
    :parents made
    :content [from-stream
              from-reader
              byte-input
              (str string-output)
              copied]
    :resource [(some? resource)
               (slurp resource)]
    :delete [deleted
             silent
             (.exists writer-file)]}))
