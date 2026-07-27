(ns gobb.fixture.entry)

(defn -main [& args]
  (prn {:mode :main
        :args args
        :file *file*}))

(defn alternate [& args]
  (prn {:mode :alternate
        :args args
        :file *file*}))

(defn exec [options]
  (prn {:mode :exec
        :options options
        :file *file*}))
