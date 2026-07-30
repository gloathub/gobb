(require '[clojure.zip :as zip])

(let [loc (zip/vector-zip [1 [2 3]])
      nested (-> loc zip/down zip/right zip/down)
      changed (-> nested (zip/replace 4) zip/root)]
  (prn {:node (zip/node nested)
        :path (zip/path nested)
        :changed changed
        :walk (->> (iterate zip/next loc)
                   (take-while (complement zip/end?))
                   (map zip/node)
                   vec)}))
