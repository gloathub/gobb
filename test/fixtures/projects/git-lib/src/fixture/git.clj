(ns fixture.git
  (:require [clojure.java.io :as io]))

(defn value []
  [:git-dependency
   (slurp (io/resource "git-resource.txt"))])
