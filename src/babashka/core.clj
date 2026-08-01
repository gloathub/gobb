(ns babashka.core)

(defn windows?
  "Returns true when Gobb is running on Windows."
  []
  (= "Windows" (System/getProperty "os.name")))
