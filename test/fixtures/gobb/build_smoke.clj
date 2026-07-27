(ns gobb.build-smoke)

(defn -main [& _]
  (println (str "gobb-smoke:" (reduce + [10 20 12]))))
