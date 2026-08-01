(ns project.entry
  (:require [babashka.curl :as curl]
            [babashka.fs :as fs]
            [clojure.java.io :as io]
            [fixture.local :as local]))

(defn -main [& args]
  (prn {:compiled (local/value)
        :runtime [(str (io/file "bundled-io"))
                  (str (fs/path "bundled-fs"))]
        :override (curl/transport)
        :args args}))
