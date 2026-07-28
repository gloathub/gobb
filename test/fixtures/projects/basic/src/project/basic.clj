(ns project.basic
  (:require [fixture.local :as local]
            [clojure.java.io :as io]))

(defn result []
  {:project :bb-edn
   :local [(local/value)
           (slurp (io/resource "local-resource.txt"))]
   :resource (slurp (io/resource "project-resource.txt"))})
