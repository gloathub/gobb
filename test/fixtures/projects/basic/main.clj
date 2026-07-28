(ns project.entry
  (:require [fixture.local :as local]))

(defn -main [& args]
  (prn {:compiled (local/value)
        :args args}))
