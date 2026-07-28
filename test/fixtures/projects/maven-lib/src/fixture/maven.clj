(ns fixture.maven
  (:require [clojure.java.io :as io]
            [fixture.transitive :as transitive]))

(defn value []
  [:maven-dependency
   (transitive/value)
   (slurp (io/resource "maven-resource.txt"))])
