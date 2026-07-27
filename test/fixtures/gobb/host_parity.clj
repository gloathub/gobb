(ns gobb.host-parity)

(defn -main [& argv]
  (prn {:ns (ns-name *ns*)
        :file *file*
        :args *command-line-args*
        :argv argv
        :cwd (System/getProperty "user.dir")
        :env (System/getenv "GOBB_PARITY_ENV")}))
