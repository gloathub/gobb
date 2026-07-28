(ns gobb.java-compat-probe)

(defn target []
  (case runtime.GOOS
    "js" :browser
    "wasip1" :wasi
    :native))

(defn stream-proof []
  (let [input (ByteArrayInputStream. (.getBytes "gobb"))
        output (ByteArrayOutputStream.)]
    (.write output (.getBytes "java"))
    {:input-stream (instance? InputStream input)
     :input-available (.available input)
     :output-stream (instance? OutputStream output)
     :output-value (.toString output)}))

(defn filesystem-proof []
  (when-not (= :browser (target))
    (let [[directory error] (os.MkdirTemp "" "gobb-java-")]
      (when error
        (throw error))
      (let [file (File. directory "proof.txt")
            path (.toPath file)]
        (try
          (Files/writeString path "portable")
          {:file (instance? File file)
           :path (instance? Path path)
           :exists (and (.exists file) (Files/exists path))
           :name (.getName file)
           :content (Files/readString path)
           :size (Files/size path)}
          (finally
            (os.RemoveAll directory)))))))

(defn language-proof []
  (let [builder (StringBuilder. "Go")]
    (.append builder " + bb!")
    {:builder (instance? StringBuilder builder)
     :value (.toString builder)
     :separator File/separator
     :exception
     (try
       (throw (Exception. "portable error"))
       (catch Exception error
         (.getMessage error)))}))

(defn -main [& _]
  (prn {:target (target)
        :language (language-proof)
        :streams (stream-proof)
        :filesystem (filesystem-proof)}))
