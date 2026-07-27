(ns gobb.host)

(def no-source-path "NO_SOURCE_PATH")
(def repl-source-path "<repl>")
(def preload-source-path "<preloads>")

(def load-paths (atom []))
(def configured-data-readers (atom {}))
(def configured-default-data-reader (atom nil))
(def shutdown-hooks (atom []))
(def shutdown-ran? (atom false))
(def current-source (atom no-source-path))

(defn initialize! []
  ;; Glojure initializes *in* and *out* at bootstrap, but its generated
  ;; clojure.core currently leaves *err* nil. Own all three roots here so the
  ;; Gobb execution host has one explicit standard-stream contract.
  (alter-var-root #'*in* (constantly os.Stdin))
  (alter-var-root #'*out* (constantly os.Stdout))
  (alter-var-root #'*err* (constantly os.Stderr))
  (in-ns 'user)
  (refer 'clojure.core))

(defn set-command-line-args! [args]
  (alter-var-root #'*command-line-args* (constantly (seq args))))

(defn set-file! [file]
  (alter-var-root #'*file* (constantly file)))

(defn add-load-path! [path]
  (swap! load-paths
         (fn [paths]
           (if (some #(= path %) paths)
             paths
             (conj paths path))))
  (add-load-path path))

(defn resolve-var! [target default-var]
  (let [target-symbol (symbol target)
        namespace-name (or (namespace target-symbol)
                           (name target-symbol))
        var-name (if (namespace target-symbol)
                   (name target-symbol)
                   default-var)
        namespace-symbol (symbol namespace-name)]
    (require namespace-symbol)
    (or (ns-resolve namespace-symbol (symbol var-name))
        (throw
         (ex-info
          (str "Could not resolve " namespace-name "/" var-name)
          {:gobb/target target})))))

(defn parse-exec-value [value]
  (cond
    (contains? #{"true" "false" "nil"} value) (read-string value)
    (re-matches #"[+-]?\d+(\.\d+)?" value) (read-string value)
    (contains? #{\( \[ \{ \: \# \"} (first value)) (read-string value)
    :else value))

(defn parse-exec-args [args]
  (loop [remaining (seq args)
         options {}]
    (if-let [argument (first remaining)]
      (cond
        (.startsWith argument "--")
        (let [equals (strings.Index argument "=")
              inline? (not= -1 equals)
              key-name (if inline?
                         (subs argument 2 equals)
                         (subs argument 2))
              next-value (second remaining)
              has-value? (and next-value
                              (not (.startsWith next-value "--")))
              value (cond
                      inline? (subs argument (inc equals))
                      has-value? next-value
                      :else "true")]
          (recur (drop (if (and (not inline?) has-value?) 2 1)
                       remaining)
                 (assoc options
                        (keyword key-name)
                        (parse-exec-value value))))

        (.startsWith argument ":")
        (if-let [value (second remaining)]
          (recur (drop 2 remaining)
                 (assoc options
                        (keyword (subs argument 1))
                        (parse-exec-value value)))
          (throw (ex-info (str argument " requires a value")
                          {:gobb/argument argument})))

        :else
        (recur (next remaining)
               (update options :args (fnil conj []) argument)))
      options)))

(defn invoke-main! [target args]
  (set-command-line-args! args)
  (binding [*file* no-source-path]
    (apply (resolve-var! target "-main") args)))

(defn invoke-exec! [target args]
  (set-command-line-args! args)
  (binding [*file* no-source-path]
    ((resolve-var! target nil) (parse-exec-args args))))

(defn register-shutdown-hook! [hook]
  (swap! shutdown-hooks conj hook)
  hook)

(defn run-shutdown-hooks! []
  (when (compare-and-set! shutdown-ran? false true)
    (doseq [hook (reverse @shutdown-hooks)]
      (hook))))

(defn exit! [status]
  (run-shutdown-hooks!)
  (os.Exit status))

(defn run-main! [main]
  (try
    (let [result (main)]
      (run-shutdown-hooks!)
      result)
    (catch Exception error
      (run-shutdown-hooks!)
      (fmt.Fprintln
       os.Stderr
       (str @current-source ": " (fmt.Sprint error)))
      (os.Exit 1))))

(defn resource-path [name]
  (some (fn [load-path]
          (let [candidate (path:filepath.Join load-path name)
                [_ error] (os.Stat candidate)]
            (when (nil? error)
              candidate)))
        @load-paths))

(defn layout-character? [character]
  (or (Character/isWhitespace character)
      (= character \,)))

(defn skip-layout [source start]
  (loop [index start]
    (if (< index (count source))
      (let [character (.charAt source index)]
        (cond
          (layout-character? character)
          (recur (inc index))

          (= character \;)
          (let [newline (strings.Index (subs source index) "\n")]
            (if (= -1 newline)
              (count source)
              (recur (+ index newline 1))))

          :else index))
      index)))

(def opening-delimiters #{\( \[ \{})
(def closing-delimiters #{\) \] \}})

(defn form-end [source start]
  (loop [index start
         depth 0
         string? false
         escaped? false
         comment? false]
    (if (>= index (count source))
      index
      (let [character (.charAt source index)]
        (cond
          comment?
          (recur (inc index) depth string? false
                 (not= character \newline))

          escaped?
          (recur (inc index) depth string? false false)

          string?
          (cond
            (= character \\)
            (recur (inc index) depth true true false)

            (= character \")
            (recur (inc index) depth false false false)

            :else
            (recur (inc index) depth true false false))

          (= character \")
          (recur (inc index) depth true false false)

          (= character \;)
          (recur (inc index) depth false false true)

          (opening-delimiters character)
          (recur (inc index) (inc depth) false false false)

          (closing-delimiters character)
          (if (= depth 1)
            (inc index)
            (recur (inc index) (dec depth) false false false))

          (and (zero? depth)
               (layout-character? character))
          index

          :else
          (recur (inc index) depth false false false))))))

(declare evaluate-source rewrite-reader-features rewrite-tagged-literals)

(defn rewrite-conditional-body [body]
  (loop [index 0
         output ""]
    (let [feature-start (skip-layout body index)]
      (if (>= feature-start (count body))
        (str output (subs body index))
        (let [feature-end (form-end body feature-start)
              value-start (skip-layout body feature-end)
              value-end (form-end body value-start)
              feature (subs body feature-start feature-end)
              replacement (if (contains? #{":bb" ":gobb" ":clj"} feature)
                            ":glj"
                            feature)]
          (recur value-end
                 (str output
                      (subs body index feature-start)
                      replacement
                      (subs body feature-end value-start)
                      (rewrite-reader-features
                       (subs body value-start value-end)))))))))

(defn rewrite-reader-features [source]
  (loop [index 0
         string? false
         escaped? false
         comment? false
         output ""]
    (if (>= index (count source))
      output
      (let [character (.charAt source index)
            remaining (subs source index)
            marker-length (cond
                            (.startsWith remaining "#?@(") 4
                            (.startsWith remaining "#?(") 3
                            :else nil)]
        (cond
          comment?
          (recur (inc index) false false
                 (not= character \newline)
                 (str output character))

          escaped?
          (recur (inc index) true false false
                 (str output character))

          string?
          (cond
            (= character \\)
            (recur (inc index) true true false
                   (str output character))

            (= character \")
            (recur (inc index) false false false
                   (str output character))

            :else
            (recur (inc index) true false false
                   (str output character)))

          (= character \")
          (recur (inc index) true false false
                 (str output character))

          (= character \;)
          (recur (inc index) false false true
                 (str output character))

          marker-length
          (let [conditional-end (form-end source index)
                body-start (+ index marker-length)
                body-end (dec conditional-end)]
            (recur conditional-end false false false
                   (str output
                        (subs source index body-start)
                        (rewrite-conditional-body
                         (subs source body-start body-end))
                        ")")))

          :else
          (recur (inc index) false false false
                 (str output character)))))))

(defn prepare-build-source [source]
  (let [main-pattern "(defn -main"]
    (when-not (strings.Contains source main-pattern)
      (throw
       (ex-info "gobb build requires a (defn -main ...) entry point"
                {:gobb/build :missing-main})))
    (str
     (-> source
         rewrite-reader-features
         (strings.Replace main-pattern "(defn gobb-user-main" 1))
     "\n\n"
     "(defn -main [& gobb-argv]\n"
     "  (alter-var-root #'*in* (constantly os.Stdin))\n"
     "  (alter-var-root #'*out* (constantly os.Stdout))\n"
     "  (alter-var-root #'*err* (constantly os.Stderr))\n"
     "  (alter-var-root #'*command-line-args*\n"
     "                  (constantly (seq gobb-argv)))\n"
     "  (binding [*ns* (or (find-ns 'user) (create-ns 'user))\n"
     "            *file* \"NO_SOURCE_PATH\"]\n"
     "    (apply gobb-user-main gobb-argv)))\n")))

(defn source-tags [source]
  (->> (re-seq #"#([A-Za-z][A-Za-z0-9_.-]*(/[A-Za-z0-9_.-]+)?)"
               source)
       (map (comp symbol second))
       set))

(defn tag-character? [character]
  (or (Character/isLetterOrDigit character)
      (contains? #{\_ \. \- \/} character)))

(defn tagged-value [tag value-source]
  (let [tag (symbol tag)
        reader (or (get @configured-data-readers tag)
                   (when-let [default-reader
                              @configured-default-data-reader]
                     (fn [value]
                       (default-reader tag value))))]
    (if reader
      (pr-str (reader (read-string
                       (rewrite-tagged-literals value-source))))
      (throw
       (ex-info (str "No reader function for tag " tag)
                {:gobb/tag tag})))))

(defn rewrite-tagged-literals [source]
  (loop [index 0
         string? false
         escaped? false
         comment? false
         output ""]
    (if (>= index (count source))
      output
      (let [character (.charAt source index)
            next-index (inc index)
            next-character (when (< next-index (count source))
                             (.charAt source next-index))]
        (cond
          comment?
          (recur next-index false false
                 (not= character \newline)
                 (str output character))

          escaped?
          (recur next-index true false false
                 (str output character))

          string?
          (cond
            (= character \\)
            (recur next-index true true false
                   (str output character))

            (= character \")
            (recur next-index false false false
                   (str output character))

            :else
            (recur next-index true false false
                   (str output character)))

          (= character \")
          (recur next-index true false false
                 (str output character))

          (= character \;)
          (recur next-index false false true
                 (str output character))

          (and (= character \#)
               next-character
               (Character/isLetter next-character))
          (let [tag-end
                (loop [tag-index next-index]
                  (if (and (< tag-index (count source))
                           (tag-character?
                            (.charAt source tag-index)))
                    (recur (inc tag-index))
                    tag-index))
                tag (subs source next-index tag-end)]
            (if (= "uuid" tag)
              (recur tag-end false false false
                     (str output "#" tag))
              (let [value-start (skip-layout source tag-end)
                    value-end (form-end source value-start)]
                (recur value-end false false false
                       (str output
                            (tagged-value
                             tag
                             (subs source value-start value-end)))))))

          :else
          (recur next-index false false false
                 (str output character)))))))

(defn install-data-readers! []
  (let [entries
        (apply merge
               (for [load-path @load-paths
                     :let [file (path:filepath.Join
                                 load-path "data_readers.clj")
                           [_ error] (os.Stat file)]
                     :when (nil? error)]
                 (read-string (slurp file))))
        readers
        (into {}
              (for [[tag target] entries]
                [tag (resolve-var! (str target) nil)]))]
    (reset! configured-data-readers readers)))

(defn run-preloads! []
  (let [preloads (strings.TrimSpace
                  (or (os.Getenv "BABASHKA_PRELOADS") ""))]
    (when-not (empty? preloads)
      (evaluate-source
       preloads
       {:file preload-source-path}))
    (swap! configured-data-readers
           merge
           (or (evaluate-source
                "*data-readers*"
                {:file preload-source-path})
               {}))
    (reset! configured-default-data-reader
            (evaluate-source
             "*default-data-reader-fn*"
             {:file preload-source-path}))))

(defn run-init! [file]
  (when file
    (let [[absolute-file error] (path:filepath.Abs file)]
      (when error
        (throw (ex-info (str "cannot resolve init file: " error)
                        {:gobb/file file})))
      (evaluate-source
       (slurp absolute-file)
       {:file absolute-file}))))

(defn evaluate-source
  [source {:keys [args file print-result?]
           :or {args ()
                file no-source-path
                print-result? false}}]
  (set-command-line-args! args)
  (reset! current-source file)
  ;; A single enclosing do lets the runtime reader accept any number of forms
  ;; while keeping them in the same Glojure environment.
  (let [source (-> source
                   rewrite-reader-features
                   rewrite-tagged-literals)
        read-and-evaluate
        (fn []
          (let [form (read-string (str "(do\n" source "\n)"))
                result (eval form)]
            (when (and print-result? (some? result))
              (prn result))
            result))]
    (binding [*file* file]
      (read-and-evaluate))))
