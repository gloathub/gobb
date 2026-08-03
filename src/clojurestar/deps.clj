(ns clojurestar.deps
  "Dialect-neutral dynamic dependency loading."
  (:require [gobb.deps :as implementation]))

(defn add-deps
  "Add the Maven dependencies in a deps.edn map to the running dialect."
  [deps-map]
  (implementation/add-deps deps-map)
  nil)
