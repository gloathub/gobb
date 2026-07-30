(ns taoensso.timbre.appenders.core)

(defn println-appender
  ([] (println-appender nil))
  ([_options]
   {:enabled? true
    :async? false
    :fn (fn [{:keys [output_]}]
          (binding [*out* *err*]
            (println (force output_))))}))
