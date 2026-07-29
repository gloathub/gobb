(ns babashka.fs
  (:require [gobb.capabilities :as capabilities]
            [gobb.host :as host]))

(def file-separator (str (go/string os.PathSeparator)))
(def path-separator (str (go/string os.PathListSeparator)))

(defn path-string [value]
  (str value))

(defn path-object [value]
  (Paths/get (str value) (into-array String [])))

(defn path
  ([value]
   (path-object value))
  ([parent child]
   (let [child (path-string child)]
     (path-object
      (cond
        (nil? parent) child
        (path:filepath.IsAbs child) child
        :else (path:filepath.Join (path-string parent) child)))))
  ([parent child & more]
   (reduce path (path parent child) more)))

(defn file
  ([value]
   (File. (path-string value)))
  ([value & more]
   (File. (path-string (apply path value more)))))

(defn fail-path! [operation value error]
  (throw
   (ex-info
    (str operation " failed for " value ": " (fmt.Sprint error))
    {:gobb/fs operation
     :path (path-string value)})))

(defn stat [value]
  (let [[info error] (os.Stat (path-string value))]
    (when error
      (fail-path! :stat value error))
    info))

(defn lstat [value]
  (let [[info error] (os.Lstat (path-string value))]
    (when error
      (fail-path! :lstat value error))
    info))

(defn windows? []
  (= "windows" runtime.GOOS))

(defn cwd []
  (path-object (capabilities/working-directory)))

(defn home
  ([]
   (path-object
    (or (not-empty (System/getProperty "user.home"))
        (os.Getenv "HOME"))))
  ([user]
   (if (empty? user)
     (home)
     (path (parent (home)) user))))

(defn temp-dir []
  (path-object (os.TempDir)))

(defn absolute? [value]
  (path:filepath.IsAbs (path-string value)))

(defn relative? [value]
  (not (absolute? value)))

(defn absolutize [value]
  (let [[result error] (path:filepath.Abs (path-string value))]
    (when error
      (fail-path! :absolutize value error))
    (path-object result)))

(defn normalize [value]
  (path-object (path:filepath.Clean (path-string value))))

(defn real-path
  ([value]
   (real-path value nil))
  ([value _options]
   (let [[result error]
         (path:filepath.EvalSymlinks (path-string value))]
     (when error
       (fail-path! :real-path value error))
     (path-object result))))

(defn canonicalize
  ([value]
   (canonicalize value nil))
  ([value options]
   (if (:nofollow-links options)
     (normalize (absolutize value))
     (real-path value))))

(defn relativize [base other]
  (let [[result error]
        (path:filepath.Rel (path-string base)
                           (path-string other))]
    (when error
      (fail-path! :relativize other error))
    (path-object
     (if (= "." result) "" result))))

(defn root [value]
  (let [value (path-string value)
        volume (path:filepath.VolumeName value)]
    (when (absolute? value)
      (path-object
       (str volume file-separator)))))

(defn parent [value]
  (let [value (path-string value)
        result (path:filepath.Dir value)]
    (when-not (or (= "." result)
                  (= result value))
      (path-object result))))

(defn file-name [value]
  (path:filepath.Base (path-string value)))

(defn components [value]
  (let [clean (path:filepath.Clean (path-string value))
        volume (path:filepath.VolumeName clean)
        without-volume (strings.TrimPrefix clean volume)
        pieces (remove empty?
                       (strings.Split
                        (strings.ReplaceAll without-volume "\\" "/")
                        "/"))
        pieces (mapv path-object pieces)]
    (seq
     (if (absolute? clean)
       (into [(path-object (str volume file-separator))] pieces)
       pieces))))

(defn starts-with? [value prefix]
  (strings.HasPrefix (path-string value) (path-string prefix)))

(defn ends-with? [value suffix]
  (strings.HasSuffix (path-string value) (path-string suffix)))

(defn exists?
  ([value]
   (exists? value nil))
  ([value _options]
   (let [[_ error] (os.Lstat (path-string value))]
     (nil? error))))

(defn directory?
  ([value]
   (directory? value nil))
  ([value options]
   (try
     (.IsDir (if (:nofollow-links options)
               (lstat value)
               (stat value)))
     (catch Exception _ false))))

(defn regular-file?
  ([value]
   (regular-file? value nil))
  ([value options]
   (try
     (.IsRegular (.Mode (if (:nofollow-links options)
                          (lstat value)
                          (stat value))))
     (catch Exception _ false))))

(defn sym-link? [value]
  (try
    (not (zero? (bit-and
                 (int (.Mode (lstat value)))
                 (int os.ModeSymlink))))
    (catch Exception _ false)))

(defn hidden? [value]
  (strings.HasPrefix (file-name value) "."))

(defn readable? [value]
  (let [[file error] (os.Open (path-string value))]
    (when file (.Close file))
    (nil? error)))

(defn writable? [value]
  (let [[file error]
        (os.OpenFile (path-string value) os.O_WRONLY 0)]
    (when file (.Close file))
    (nil? error)))

(defn executable? [value]
  (try
    (let [mode (int (.Mode (stat value)))]
      (and (not (directory? value))
           (not (zero? (bit-and mode 73)))))
    (catch Exception _ false)))

(def permission-names
  ["OWNER_READ"
   "OWNER_WRITE"
   "OWNER_EXECUTE"
   "GROUP_READ"
   "GROUP_WRITE"
   "GROUP_EXECUTE"
   "OTHERS_READ"
   "OTHERS_WRITE"
   "OTHERS_EXECUTE"])

(def permission-bits
  [0400 0200 0100 0040 0020 0010 0004 0002 0001])

(defn str->posix [value]
  (when-not (and (= 9 (count value))
                 (every?
                  true?
                  (map-indexed
                   (fn [index character]
                     (or (= \- character)
                         (= character
                            (nth "rwxrwxrwx" index))))
                   value)))
    (throw
     (ex-info
      (str "Invalid mode: " value)
      {:gobb/fs :str->posix
       :mode value})))
  (into
   #{}
   (keep-indexed
    (fn [index character]
      (when-not (= \- character)
        (symbol (nth permission-names index))))
    value)))

(defn posix->str [permissions]
  (let [permissions (set (map str permissions))]
    (apply
     str
     (map-indexed
      (fn [index character]
        (if (contains? permissions
                       (nth permission-names index))
          character
          \-))
      "rwxrwxrwx"))))

(defn permission-mode [permissions]
  (let [permissions
        (if (string? permissions)
          (str->posix permissions)
          permissions)
        permissions (set (map str permissions))]
    (reduce
     bit-or
     0
     (keep-indexed
      (fn [index permission]
        (when (contains? permissions permission)
          (nth permission-bits index)))
      permission-names))))

(defn set-posix-file-permissions [value permissions]
  (capabilities/require! :filesystem :write)
  (let [error
        (os.Chmod (path-string value)
                  (permission-mode permissions))]
    (when error
      (fail-path! :set-posix-file-permissions value error))
    (path value)))

(defn posix-file-permissions
  ([value]
   (posix-file-permissions value nil))
  ([value options]
   (let [mode
         (int
          (.Mode
           (if (:nofollow-links options)
             (lstat value)
             (stat value))))]
     (into
      #{}
      (keep-indexed
       (fn [index permission]
         (when-not
          (zero? (bit-and mode
                          (nth permission-bits index)))
           (symbol permission)))
       permission-names)))))

(defn size [value]
  (.Size (stat value)))

(defn same-file? [left right]
  (os.SameFile (stat left) (stat right)))

(defn create-dir
  ([directory]
   (create-dir directory nil))
  ([directory options]
   (capabilities/require! :filesystem :write)
   (let [mode (or (:mode options)
                  (when-let [permissions
                             (:posix-file-permissions options)]
                    (permission-mode permissions))
                  0777)
         error (os.Mkdir (path-string directory) mode)]
     (when error
       (fail-path! :create-dir directory error))
     (path directory))))

(defn create-dirs
  ([directory]
   (create-dirs directory nil))
  ([directory options]
   (capabilities/require! :filesystem :write)
   (let [mode (or (:mode options)
                  (when-let [permissions
                             (:posix-file-permissions options)]
                    (permission-mode permissions))
                  0777)
         error (os.MkdirAll (path-string directory) mode)]
     (when error
       (fail-path! :create-dirs directory error))
     (path directory))))

(defn create-file
  ([value]
   (create-file value nil))
  ([value options]
   (capabilities/require! :filesystem :write)
   (let [[stream error]
         (os.OpenFile (path-string value)
                      (bit-or os.O_CREATE os.O_EXCL os.O_WRONLY)
                      (or (:mode options)
                          (when-let [permissions
                                     (:posix-file-permissions options)]
                            (permission-mode permissions))
                          0666))]
     (when error
       (fail-path! :create-file value error))
     (.Close stream)
     (path value))))

(defn create-temp-dir
  ([]
   (create-temp-dir {}))
  ([options]
   (capabilities/require! :filesystem :write)
   (let [directory (or (:dir options)
                       (:path options)
                       (os.TempDir))
         prefix (or (:prefix options) "")
         [result error]
         (os.MkdirTemp (path-string directory) prefix)]
     (when error
       (fail-path! :create-temp-dir directory error))
     (when-let [permissions (:posix-file-permissions options)]
       (set-posix-file-permissions result permissions))
     (path-object result))))

(defn create-temp-file
  ([]
   (create-temp-file {}))
  ([options]
   (capabilities/require! :filesystem :write)
   (let [directory (or (:dir options)
                       (:path options)
                       (os.TempDir))
         pattern (str (or (:prefix options) "")
                      "*"
                      (or (:suffix options) ""))
         [stream error]
         (os.CreateTemp (path-string directory) pattern)]
     (when error
       (fail-path! :create-temp-file directory error))
     (let [result (.Name stream)]
       (.Close stream)
       (when-let [permissions (:posix-file-permissions options)]
         (set-posix-file-permissions result permissions))
       (path-object result)))))

(defmacro with-temp-dir
  [[binding options & more] & body]
  {:pre [(empty? more) (symbol? binding)]}
  `(let [options# ~(or options {})
         ~binding (create-temp-dir options#)]
     (try
       ~@body
       (finally
         (when-not (:keep options#)
           (delete-tree ~binding {:force true}))))))

(defn list-dir
  ([directory]
   (list-dir directory nil))
  ([directory glob-or-accept]
   (capabilities/require! :filesystem :read)
   (let [[entries error] (os.ReadDir (path-string directory))]
     (when error
       (fail-path! :list-dir directory error))
     (->> entries
          (map #(path directory (.Name %)))
          (filter
           (cond
             (nil? glob-or-accept) (constantly true)
             (string? glob-or-accept)
             (fn [candidate]
               (let [[matched error]
                     (path:filepath.Match
                      glob-or-accept
                      (file-name candidate))]
                 (and (nil? error) matched)))
             :else glob-or-accept))
          vec))))

(defn list-dirs [directories glob-or-accept]
  (mapcat #(list-dir % glob-or-accept) directories))

(def visitor-results
  #{:continue :skip-subtree :skip-siblings :terminate})

(defn visitor-result [result]
  (let [result (or result :continue)]
    (when-not (contains? visitor-results result)
      (throw
       (ex-info
        (str "Invalid file visitor result: " result)
        {:gobb/fs :walk-file-tree
         :result result})))
    result))

(defn walk-file-tree
  ([value]
   (walk-file-tree value nil))
  ([value options]
   (let [root (path value)
         follow-links (:follow-links options)
         max-depth (or (:max-depth options) 2147483647)
         pre-visit-dir (or (:pre-visit-dir options)
                           (fn [_ _] :continue))
         post-visit-dir (or (:post-visit-dir options)
                            (fn [_ _] :continue))
         visit-file (or (:visit-file options)
                        (fn [_ _] :continue))
         visit-file-failed
         (or (:visit-file-failed options)
             (fn [_ _] :continue))]
     (letfn [(visit [candidate depth]
               (try
                 (let [link? (sym-link? candidate)
                       directory (and (or follow-links (not link?))
                                      (directory? candidate))
                       info (if (and link? (not follow-links))
                              (lstat candidate)
                              (stat candidate))]
                   (if directory
                     (let [before
                           (visitor-result
                            (pre-visit-dir candidate info))]
                       (case before
                         :terminate :terminate
                         :skip-siblings :skip-siblings
                         :skip-subtree
                         (visitor-result
                          (post-visit-dir candidate nil))
                         (let [children-result
                               (if (< depth max-depth)
                                 (loop [children
                                        (seq (list-dir candidate))]
                                   (if-let [child (first children)]
                                     (let [result
                                           (visit child (inc depth))]
                                       (case result
                                         :terminate :terminate
                                         :skip-siblings :continue
                                         (recur (next children))))
                                     :continue))
                                 :continue)]
                           (if (= :terminate children-result)
                             :terminate
                             (visitor-result
                              (post-visit-dir candidate nil))))))
                     (visitor-result (visit-file candidate info))))
                 (catch Exception error
                   (visitor-result
                    (visit-file-failed candidate error)))))]
       (visit root 0)
       root))))

(defn path-parts [value]
  (vec
   (remove empty?
           (strings.Split
            (strings.ReplaceAll (str value) "\\" "/")
            "/"))))

(defn glob-segment? [pattern candidate]
  (let [[matched error]
        (path:filepath.Match pattern candidate)]
    (and (nil? error) matched)))

(defn glob-parts? [patterns candidates]
  (cond
    (empty? patterns) (empty? candidates)
    (= "**" (first patterns))
    (if (next patterns)
      (and (seq candidates)
           (or (glob-parts? patterns (rest candidates))
               (glob-parts? (rest patterns)
                            (rest candidates))))
      true)
    (empty? candidates) false
    (glob-segment? (first patterns) (first candidates))
    (glob-parts? (rest patterns) (rest candidates))
    :else false))

(defn path-hidden? [relative]
  (some #(strings.HasPrefix % ".")
        (path-parts relative)))

(defn match
  ([root-dir pattern]
   (match root-dir pattern nil))
  ([root-dir pattern options]
   (let [root-dir (path root-dir)
         recursive (:recursive options false)
         hidden (:hidden options false)
         max-depth (if recursive
                     (or (:max-depth options) 2147483647)
                     1)
         [kind pattern]
         (cond
           (strings.HasPrefix pattern "glob:")
           [:glob (subs pattern 5)]
           (strings.HasPrefix pattern "regex:")
           [:regex (subs pattern 6)]
         :else [:glob pattern])
         regex
         (when (= :regex kind)
           (let [[compiled error] (regexp.Compile pattern)]
             (when error
               (throw
                (ex-info
                 (str "Invalid regex pattern: " pattern)
                 {:gobb/fs :match
                  :pattern pattern})))
             compiled))
         matches (atom [])]
     (walk-file-tree
      root-dir
      {:max-depth max-depth
       :follow-links (:follow-links options)
       :pre-visit-dir
       (fn [candidate _]
         (if (= (str candidate) (str root-dir))
           :continue
           (let [relative (str (relativize root-dir candidate))]
             (if (and (not hidden) (path-hidden? relative))
               :skip-subtree
               (do
                 (when
                  (if (= :regex kind)
                    (.MatchString regex relative)
                    (glob-parts? (path-parts pattern)
                                 (path-parts relative)))
                   (swap! matches conj candidate))
                 (if recursive :continue :skip-subtree))))))
       :visit-file
       (fn [candidate _]
         (let [relative (str (relativize root-dir candidate))]
           (when
            (and (or hidden (not (path-hidden? relative)))
                 (if (= :regex kind)
                   (.MatchString regex relative)
                   (glob-parts? (path-parts pattern)
                                (path-parts relative))))
             (swap! matches conj candidate)))
         :continue)})
     @matches)))

(defn glob
  ([root-dir pattern]
   (glob root-dir pattern nil))
  ([root-dir pattern options]
   (let [recursive
         (:recursive
          options
          (or (strings.Contains pattern "**")
              (strings.Contains
               (strings.ReplaceAll pattern "\\" "/")
               "/")))
         hidden
         (:hidden options
                  (strings.HasPrefix pattern "."))]
     (match root-dir
            (str "glob:" pattern)
            (assoc options
                   :recursive recursive
                   :hidden hidden)))))

(defn read-all-bytes [value]
  (capabilities/read-bytes (path-string value)))

(defn read-all-lines
  ([value]
   (read-all-lines value nil))
  ([value _options]
   (let [content (go/string (read-all-bytes value))]
     (if (empty? content)
       []
       (vec
        (strings.Split
         (strings.TrimSuffix content "\n")
         "\n"))))))

(defn write-bytes
  ([value bytes]
   (write-bytes value bytes nil))
  ([value bytes options]
   (capabilities/require! :filesystem :write)
   (let [flags (bit-or os.O_CREATE os.O_WRONLY
                       (if (:append options)
                         os.O_APPEND
                         os.O_TRUNC))
         [stream error]
         (os.OpenFile (path-string value) flags 0666)]
     (when error
       (fail-path! :write-bytes value error))
     (let [[_ error] (.Write stream bytes)]
       (.Close stream)
       (when error
         (fail-path! :write-bytes value error)))
     (path value))))

(defn write-lines
  ([value lines]
   (write-lines value lines nil))
  ([value lines options]
   (write-bytes
    value
    (.getBytes
     (str (strings.Join (mapv str lines)
                        (System/lineSeparator))
          (when (seq lines) (System/lineSeparator))))
    options)))

(defn update-file [value f & arguments]
  (let [[options f arguments]
        (if (map? f)
          [f (first arguments) (rest arguments)]
          [nil f arguments])
        old-value (slurp (file value))
        new-value (apply f old-value arguments)]
    (spit (file value) new-value)
    new-value))

(defn touch
  ([value]
   (touch value nil))
  ([value options]
   (when-not (exists? value)
     (create-file value))
   (let [milliseconds (or (:time options)
                          (.UnixMilli (time.Now)))
         timestamp (time.UnixMilli milliseconds)
         error (os.Chtimes (path-string value)
                           timestamp timestamp)]
     (when error
       (fail-path! :touch value error))
     (path value))))

(defn delete [value]
  (capabilities/require! :filesystem :remove)
  (let [error (os.Remove (path-string value))]
    (when error
      (fail-path! :delete value error))
    nil))

(defn delete-if-exists [value]
  (if (exists? value {:nofollow-links true})
    (do (delete value) true)
    false))

(defn delete-tree
  ([value]
   (delete-tree value nil))
  ([value _options]
   (capabilities/require! :filesystem :remove)
   (let [error (os.RemoveAll (path-string value))]
     (when error
       (fail-path! :delete-tree value error))
     nil)))

(defn delete-on-exit [value]
  (host/register-shutdown-hook!
   #(delete-tree value {:force true}))
  (path value))

(defn copy
  ([source target]
   (copy source target nil))
  ([source target options]
   (capabilities/require! :filesystem :write)
   (let [target (if (directory? target)
                  (path target (file-name source))
                  (path target))]
     (when (and (exists? target)
                (not (:replace-existing options)))
       (throw
        (ex-info (str "Target already exists: " target)
                 {:gobb/fs :copy
                  :path (str target)})))
     (let [content (read-all-bytes source)
           mode (int (.Mode (stat source)))
           error (os.WriteFile (str target) content mode)]
       (when error
         (fail-path! :copy target error))
       target))))

(defn copy-tree
  ([source target]
   (copy-tree source target nil))
  ([source target options]
   (when-not (directory? source)
     (throw
      (ex-info (str "Not a directory: " source)
               {:gobb/fs :copy-tree
                :path (str source)})))
   (create-dirs target)
   (doseq [entry (list-dir source)]
     (let [destination (path target (file-name entry))]
       (if (directory? entry {:nofollow-links true})
         (copy-tree entry destination options)
         (copy entry destination options))))
   (path target)))

(defn move
  ([source target]
   (move source target nil))
  ([source target options]
   (capabilities/require! :filesystem :write)
   (let [target (if (directory? target)
                  (path target (file-name source))
                  (path target))]
     (when (and (:replace-existing options)
                (exists? target))
       (delete-tree target))
     (let [error (os.Rename (path-string source)
                            (path-string target))]
       (when error
         (fail-path! :move source error))
       target))))

(defn create-sym-link [link target]
  (let [error (os.Symlink (path-string target)
                          (path-string link))]
    (when error
      (fail-path! :create-sym-link link error))
    (path link)))

(defn create-link [link existing]
  (let [error (os.Link (path-string existing)
                       (path-string link))]
    (when error
      (fail-path! :create-link link error))
    (path link)))

(defn read-link [link]
  (let [[target error] (os.Readlink (path-string link))]
    (when error
      (fail-path! :read-link link error))
    (path-object target)))

(defn copy-stream! [input output operation value]
  (let [[written error] (io.Copy output input)]
    (when error
      (fail-path! operation value error))
    written))

(defn gzip
  ([source-file]
   (gzip source-file {}))
  ([source-file options]
   (let [destination-directory
         (or (:dir options)
             (parent source-file)
             "")
         destination-name
         (str (or (:out-file options)
                  (str (file-name source-file) ".gz")))
         output-file
         (path destination-directory destination-name)]
     (when-let [directory (parent output-file)]
       (create-dirs directory))
     (let [[input input-error]
           (os.Open (path-string source-file))]
       (when input-error
         (fail-path! :gzip source-file input-error))
       (let [[output output-error]
             (os.Create (path-string output-file))]
         (when output-error
           (.Close input)
           (fail-path! :gzip output-file output-error))
         (let [compressor (compress:gzip.NewWriter output)]
           (copy-stream! input compressor :gzip source-file)
           (let [close-error (.Close compressor)]
             (.Close input)
             (.Close output)
             (when close-error
               (fail-path! :gzip output-file close-error))))))
     (str output-file))))

(defn gunzip
  ([gz-file]
   (gunzip gz-file nil))
  ([gz-file target-dir]
   (gunzip gz-file target-dir {}))
  ([gz-file target-dir options]
   (let [destination-directory
         (or target-dir (parent gz-file) "")
         destination-name
         (strings.TrimSuffix (file-name gz-file) ".gz")
         output-file
         (path destination-directory destination-name)]
     (when (not-empty (str destination-directory))
       (create-dirs destination-directory))
     (when (and (exists? output-file)
                (not (:replace-existing options)))
       (throw
        (ex-info
         (str "Target already exists: " output-file)
         {:gobb/fs :gunzip
          :path (str output-file)})))
     (let [[input input-error]
           (os.Open (path-string gz-file))]
       (when input-error
         (fail-path! :gunzip gz-file input-error))
       (let [[decompressor gzip-error]
             (compress:gzip.NewReader input)]
         (when gzip-error
           (.Close input)
           (fail-path! :gunzip gz-file gzip-error))
         (let [[output output-error]
               (os.Create (path-string output-file))]
           (when output-error
             (.Close decompressor)
             (.Close input)
             (fail-path! :gunzip output-file output-error))
           (let [written
                 (copy-stream! decompressor output
                               :gunzip gz-file)]
             (.Close output)
             (.Close decompressor)
             (.Close input)
             written)))))))

(defn tree-entries [value]
  (let [entries (atom [])]
    (if (directory? value {:nofollow-links true})
      (walk-file-tree
       value
       {:pre-visit-dir
        (fn [candidate _]
          (swap! entries conj candidate)
          :continue)
        :visit-file
        (fn [candidate _]
          (swap! entries conj candidate)
          :continue)})
      (swap! entries conj (path value)))
    @entries))

(defn zip-entry-name [candidate options]
  (let [directory (directory? candidate)
        candidate
        (if-let [root (:root options)]
          (str (relativize root candidate))
          (str candidate))
        candidate
        (if-let [path-fn (:path-fn options)]
          (path-fn candidate)
          candidate)
        candidate
        (when candidate
          (strings.ReplaceAll candidate "\\" "/"))]
    (when (and candidate (not (empty? candidate)))
      (if (and directory
               (not (strings.HasSuffix candidate "/")))
        (str candidate "/")
        candidate))))

(defn zip
  ([zip-file path-or-paths]
   (zip zip-file path-or-paths nil))
  ([zip-file path-or-paths options]
   (let [entries
         (if (or (string? path-or-paths)
                 (instance? File path-or-paths)
                 (instance? Path path-or-paths))
           [path-or-paths]
           path-or-paths)]
     (assert (every? relative? entries)
             "All entries must be relative")
     (let [[output output-error]
           (os.Create (path-string zip-file))]
       (when output-error
         (fail-path! :zip zip-file output-error))
       (let [archive (archive:zip.NewWriter output)
             archive-path
             (str (normalize (absolutize zip-file)))]
         (doseq [entry entries
                 candidate (tree-entries entry)]
           (when-not
            (= archive-path
               (str (normalize (absolutize candidate))))
             (when-let [entry-name
                        (zip-entry-name candidate options)]
               (let [[destination create-error]
                     (.Create archive entry-name)]
                 (when create-error
                   (.Close archive)
                   (.Close output)
                   (fail-path! :zip zip-file create-error))
                 (when-not (directory? candidate)
                   (let [[input input-error]
                         (os.Open (path-string candidate))]
                     (when input-error
                       (.Close archive)
                       (.Close output)
                       (fail-path! :zip candidate input-error))
                     (copy-stream! input destination
                                   :zip candidate)
                     (.Close input)))))))
         (let [close-error (.Close archive)]
           (.Close output)
           (when close-error
             (fail-path! :zip zip-file close-error)))))
     nil)))

(defn safe-unzip-path [target-dir entry-name]
  (let [target (normalize (absolutize target-dir))
        output (normalize (absolutize (path target entry-name)))
        relative (str (relativize target output))]
    (when (or (absolute? entry-name)
              (= ".." relative)
              (strings.HasPrefix relative
                                 (str ".." file-separator)))
      (throw
       (ex-info
        (str "Zip entry escapes target directory: " entry-name)
        {:gobb/fs :unzip
         :entry entry-name})))
    output))

(defn host-field [value field]
  (let [[result found]
        (github.com:glojurelang:glojure:pkg:lang.FieldOrMethod
         value field)]
    (when-not found
      (throw
       (ex-info
        (str "Missing Go field " field)
        {:gobb/fs :host-field
         :field field})))
    result))

(defn unzip
  ([zip-file]
   (unzip zip-file "."))
  ([zip-file target-dir]
   (unzip zip-file target-dir nil))
  ([zip-file target-dir options]
   (create-dirs target-dir)
   (let [[archive archive-error]
         (archive:zip.OpenReader (path-string zip-file))]
     (when archive-error
       (fail-path! :unzip zip-file archive-error))
     (doseq [entry (host-field archive "File")]
       (let [entry-name (host-field entry "Name")
             output-file (safe-unzip-path target-dir entry-name)]
         (if (strings.HasSuffix entry-name "/")
           (create-dirs output-file)
           (when (or (nil? (:extract-fn options))
                     ((:extract-fn options)
                      {:entry entry :name entry-name}))
             (when-let [directory (parent output-file)]
               (create-dirs directory))
             (when (and (exists? output-file)
                        (not (:replace-existing options)))
               (.Close archive)
               (throw
                (ex-info
                 (str "Target already exists: " output-file)
                 {:gobb/fs :unzip
                  :path (str output-file)})))
             (let [[input input-error] (.Open entry)]
               (when input-error
                 (.Close archive)
                 (fail-path! :unzip entry-name input-error))
               (let [[output output-error]
                     (os.Create (path-string output-file))]
                 (when output-error
                   (.Close input)
                   (.Close archive)
                   (fail-path! :unzip output-file output-error))
                 (copy-stream! input output
                               :unzip entry-name)
                 (.Close output)
                 (.Close input)))))))
     (.Close archive)
     nil)))

(defn split-ext
  ([value]
   (split-ext value nil))
  ([value options]
   (let [value (str value)
         name (file-name value)
         requested (:ext options)
         extension (if requested
                     (str "." requested)
                     (not-empty (path:filepath.Ext name)))]
     (if (and extension
              (strings.HasSuffix value extension)
              (not= name extension))
       [(subs value 0 (- (count value) (count extension)))
        (subs extension 1)]
       [value nil]))))

(defn strip-ext
  ([value]
   (strip-ext value nil))
  ([value options]
   (first (split-ext value options))))

(defn extension [value]
  (second (split-ext value)))

(defn split-paths [value]
  (mapv path-object (path:filepath.SplitList value)))

(defn exec-paths []
  (split-paths (or (os.Getenv "PATH") "")))

(defn which
  ([program]
   (which program nil))
  ([program options]
   (let [program (str program)
         paths (or (:paths options) (exec-paths))
         direct? (or (absolute? program)
                     (not= program (file-name program)))
         candidates (if direct?
                      [(path program)]
                      (map #(path % program) paths))
         matches (filter #(and (executable? %)
                               (not (directory? %)))
                         candidates)]
     (if (:all options)
       (vec matches)
       (first matches)))))

(defn which-all
  ([program]
   (which-all program nil))
  ([program options]
   (which program (assoc options :all true))))

(defn expand-home [value]
  (let [value (str value)]
    (if (strings.HasPrefix value "~")
      (let [separator (strings.Index value file-separator)
            username (if (neg? separator)
                       (subs value 1)
                       (subs value 1 separator))
            base (home username)]
        (if (neg? separator)
          base
          (path base (subs value (inc separator)))))
      (path value))))

(defn unixify [value]
  (strings.ReplaceAll (str value) "\\" "/"))

(defn xdg-home [environment fallback application]
  (let [configured (os.Getenv environment)
        base (if (and (not (empty? configured))
                      (absolute? configured))
               (path configured)
               (apply path (home) fallback))]
    (if (seq application)
      (path base application)
      base)))

(defn xdg-config-home
  ([] (xdg-config-home nil))
  ([application]
   (xdg-home "XDG_CONFIG_HOME" [".config"] application)))

(defn xdg-cache-home
  ([] (xdg-cache-home nil))
  ([application]
   (xdg-home "XDG_CACHE_HOME" [".cache"] application)))

(defn xdg-data-home
  ([] (xdg-data-home nil))
  ([application]
   (xdg-home "XDG_DATA_HOME" [".local" "share"] application)))

(defn xdg-state-home
  ([] (xdg-state-home nil))
  ([application]
   (xdg-home "XDG_STATE_HOME" [".local" "state"] application)))
