(ns gobb.project
  (:require [gobb.capabilities :as capabilities]
            [gobb.host :as host]
            [gobb.version]))

(def resolved-classpath (atom []))
(def configured-classpath (atom ""))
(def resolved-config (atom nil))

(def task-order-key ::task-order)

(defn exists? [path]
  (let [[_ error] (os.Stat path)]
    (nil? error)))

(defn absolute [path]
  (let [[value error] (path:filepath.Abs path)]
    (when error
      (throw
       (ex-info
        (str "cannot resolve path " path ": " error)
        {:gobb/project :invalid-path
         :path path})))
    value))

(defn config-error [path message]
  (throw
   (ex-info
    (str "Error during loading " path ": " message)
    {:gobb/project :invalid-config
     :path path})))

(defn map-forms [source map-start]
  (let [map-end (host/form-end source map-start)]
    (loop [index (inc map-start)
           forms []]
      (let [key-start (host/skip-layout source index)]
        (if (>= key-start (dec map-end))
          forms
          (let [key-end (host/form-end source key-start)
                value-start (host/skip-layout source key-end)
                value-end (host/form-end source value-start)]
            (recur value-end
                   (conj forms
                         [(subs source key-start key-end)
                          (subs source value-start value-end)]))))))))

(defn task-order-from-source [source]
  (let [start (host/skip-layout source 0)]
    (when (and (< start (count source))
               (= \{ (.charAt source start)))
      (some
       (fn [[key-source value-source]]
         (when (= :tasks (read-string key-source))
           (let [task-start (host/skip-layout value-source 0)]
             (when (and (< task-start (count value-source))
                        (= \{ (.charAt value-source task-start)))
               (->> (map-forms value-source task-start)
                    (map (comp read-string first))
                    (filter symbol?)
                    vec)))))
       (map-forms source start)))))

(defn read-config [path]
  (try
    (let [source (slurp path)
          start (host/skip-layout source 0)]
      (if (>= start (count source))
        {}
        (let [end (host/form-end source start)
              trailing (host/skip-layout source end)
              value (read-string (subs source start end))]
          (when (< trailing (count source))
            (config-error
             path
             "configuration should contain zero or one form"))
          (when-not (map? value)
            (config-error path "expected an EDN map"))
          (if-let [task-order (task-order-from-source source)]
            (assoc value task-order-key task-order)
            value))))
    (catch Exception error
      (if (= :invalid-config
             (:gobb/project (ex-data error)))
        (throw error)
        (config-error path (fmt.Sprint error))))))

(defn deep-merge [& maps]
  (letfn [(merge-values [left right]
            (if (and (map? left) (map? right))
              (merge-with merge-values left right)
              right))]
    (reduce #(merge-with merge-values %1 %2) {} (remove nil? maps))))

(defn invoked-file [argv]
  (let [candidate (if (= "build" (first argv))
                    (second argv)
                    (first argv))]
    (when (and candidate
               (not (.startsWith (str candidate) "-"))
               (exists? candidate))
      (absolute candidate))))

(defn config-directory [argv]
  (if-let [file (invoked-file argv)]
    (path:filepath.Dir file)
    (absolute ".")))

(defn discover-configs [argv explicit]
  (if explicit
    (let [path (absolute explicit)]
      (when-not (exists? path)
        (config-error path "file does not exist"))
      [path])
    (let [adjacent (config-directory argv)
          current (absolute ".")
          directories (if (= adjacent current)
                        [current]
                        [adjacent current])]
      (or
       (some
        (fn [directory]
          (let [deps (path:filepath.Join directory "deps.edn")
                bb (path:filepath.Join directory "bb.edn")
                found (cond-> []
                        (exists? deps) (conj deps)
                        (exists? bb) (conj bb))]
            (when (seq found) found)))
        directories)
       []))))

(defn apply-alias [config alias]
  (let [alias (if (keyword? alias) alias (keyword alias))
        settings (get-in config [:aliases alias])]
    (when-not settings
      (throw
       (ex-info
        (str "Unknown alias: " alias)
        {:gobb/project :unknown-alias
         :alias alias})))
    (let [paths (cond
                  (:replace-paths settings) (:replace-paths settings)
                  :else (concat (:paths config)
                                (:extra-paths settings)))
          deps (cond
                 (:replace-deps settings) (:replace-deps settings)
                 :else (merge (:deps config)
                              (:extra-deps settings)))
          deps (merge deps (:override-deps settings))]
      (assoc config :paths (vec paths) :deps deps))))

(defn apply-aliases [config aliases]
  (reduce apply-alias config aliases))

(defn version-parts [value]
  (mapv
   (fn [part]
     (let [[number error] (strconv.Atoi part)]
       (if error 0 number)))
   (take 3 (concat (strings.Split (str value) ".")
                   ["0" "0" "0"]))))

(defn version-at-least? [current required]
  (not (neg? (compare (version-parts current)
                      (version-parts required)))))

(defn warn-minimum-version! [config]
  (when-let [required (:min-bb-version config)]
    (when-not (version-at-least?
               gobb.version/babashka-version required)
      (fmt.Fprintln
       os.Stderr
       (str "WARNING: this project requires babashka "
            required " or newer, but you have: "
            gobb.version/babashka-version)))))

(defn cache-root []
  (or (not-empty (os.Getenv "GOBB_CACHE"))
      (when-let [root (not-empty (os.Getenv "XDG_CACHE_HOME"))]
        (path:filepath.Join root "gobb"))
      (when-let [home (not-empty (os.Getenv "HOME"))]
        (path:filepath.Join home ".cache" "gobb"))
      (path:filepath.Join (os.TempDir) "gobb-cache")))

(defn safe-name [value]
  (strings.Map
   (fn [character]
     (if (or (Character/isLetterOrDigit character)
             (contains? #{\. \- \_} character))
       character
       \_))
   (str value)))

(defn run-command! [description args]
  (let [command (apply os:exec.Command args)
        [output error] (.CombinedOutput command)]
    (when error
      (throw
       (ex-info
        (str description " failed"
             (let [details (strings.TrimSpace (go/string output))]
               (if (empty? details)
                 (str ": " error)
                 (str ":\n" details))))
        {:gobb/project :command-failed
         :command args})))
    (strings.TrimSpace (go/string output))))

(defn ensure-native-resolution! [kind]
  (when-not (= :native (capabilities/target))
    (throw
     (ex-info
      (str (name kind) " dependency resolution is unavailable on "
           (name (capabilities/target)))
      {:gobb/unsupported-capability kind
       :target (capabilities/target)}))))

(defn add-path! [paths root value]
  (let [path (absolute (if (path:filepath.IsAbs value)
                         value
                         (path:filepath.Join root value)))]
    (when-not (exists? path)
      (throw
       (ex-info
        (str "Classpath path does not exist: " path)
        {:gobb/project :missing-path
         :path path})))
    (swap! paths #(if (some #{path} %) % (conj % path)))
    path))

(declare resolve-config! resolve-dependency!)

(defn dependency-config [root]
  (let [deps (path:filepath.Join root "deps.edn")
        bb (path:filepath.Join root "bb.edn")]
    (cond
      (exists? bb) [bb (read-config bb)]
      (exists? deps) [deps (read-config deps)]
      :else [nil {:paths ["src"]}])))

(defn resolve-local! [paths seen root coordinate]
  (let [local-root (:local/root coordinate)
        dependency-root
        (absolute
         (if (path:filepath.IsAbs local-root)
           local-root
           (path:filepath.Join root local-root)))]
    (when-not (exists? dependency-root)
      (throw
       (ex-info
        (str "Local dependency does not exist: " dependency-root)
        {:gobb/project :missing-local-dependency
         :path dependency-root})))
    (let [[config-path config] (dependency-config dependency-root)]
      (resolve-config! paths seen
                       (or config-path
                           (path:filepath.Join dependency-root "deps.edn"))
                       config
                       dependency-root))))

(defn inferred-git-url [library]
  (let [value (str library)]
    (when (.startsWith value "io.github.")
      (let [parts (strings.Split value "/")
            owner (strings.TrimPrefix (first parts) "io.github.")
            repository (second parts)]
        (when (and owner repository)
          (str "https://github.com/" owner "/" repository ".git"))))))

(defn resolve-git! [paths seen _root library coordinate]
  (ensure-native-resolution! :git-dependencies)
  (let [url (or (:git/url coordinate)
                (inferred-git-url library))
        revision (or (:git/sha coordinate)
                     (:sha coordinate)
                     (:git/tag coordinate)
                     (:tag coordinate))]
    (when-not url
      (throw
       (ex-info
        (str "Git dependency " library " requires :git/url")
        {:gobb/project :invalid-git-dependency
         :library library})))
    (when-not revision
      (throw
       (ex-info
        (str "Git dependency " library
             " requires :git/sha or :git/tag")
        {:gobb/project :invalid-git-dependency
         :library library})))
    (let [checkout (path:filepath.Join
                    (cache-root) "gitlibs"
                    (safe-name library)
                    (safe-name revision))]
      (when-not (exists? checkout)
        (os.MkdirAll (path:filepath.Dir checkout) 0755)
        (run-command! (str "Cloning " library)
                      ["git" "clone" "--quiet" url checkout])
        (run-command! (str "Checking out " library " " revision)
                      ["git" "-C" checkout "checkout" "--quiet" revision]))
      (let [dependency-root
            (if-let [subdir (:deps/root coordinate)]
              (path:filepath.Join checkout subdir)
              checkout)
            [config-path config] (dependency-config dependency-root)]
        (resolve-config! paths seen
                         (or config-path
                             (path:filepath.Join dependency-root "deps.edn"))
                         config
                         dependency-root)))))

(def default-repositories
  ["https://repo1.maven.org/maven2"
   "https://repo.clojars.org"])

(defn repository-urls [config coordinate]
  (let [repositories (:mvn/repos config)
        explicit (:mvn/repo coordinate)
        selected (when explicit
                   (or (get repositories explicit)
                       (get repositories (str explicit))))
        configured
        (for [[_ repository] repositories
              :let [url (if (string? repository)
                          repository
                          (:url repository))]
              :when url]
          url)]
    (vec
     (distinct
      (if explicit
        [(or (when (string? selected) selected)
             (:url selected)
             explicit)]
        (concat configured default-repositories))))))

(defn maven-parts [library version]
  (let [group (namespace library)
        artifact (name library)]
    (when-not group
      (throw
       (ex-info
        (str "Maven library must be qualified: " library)
        {:gobb/project :invalid-maven-coordinate
         :library library})))
    {:relative
     (str (strings.ReplaceAll group "." "/") "/"
          artifact "/" version "/" artifact "-" version)}))

(defn try-download! [url output]
  (if (exists? output)
    true
    (do
      (os.MkdirAll (path:filepath.Dir output) 0755)
      (let [temporary (str output ".part")
            command (os:exec.Command
                     "curl" "-fsSL" "--retry" "2"
                     "-o" temporary url)
            [_ error] (.CombinedOutput command)]
        (if error
          (do
            (os.Remove temporary)
            false)
          (let [rename-error (os.Rename temporary output)]
            (when rename-error
              (throw
               (ex-info
                (str "cannot cache " output ": " rename-error)
                {:gobb/project :cache-write-failed
                 :path output})))
            true))))))

(defn download-artifact! [repositories relative extension output required?]
  (or
   (exists? output)
   (some
    #(try-download!
      (str (strings.TrimRight % "/")
           "/" relative "." extension)
      output)
    repositories)
   (when required?
     (throw
      (ex-info
       (str "Could not download " relative "." extension
            " from " (strings.Join repositories ", "))
       {:gobb/project :artifact-not-found
        :artifact relative
        :repositories repositories}))))
  output)

(defn xml-tag [text tag]
  (some-> (re-find
           (re-pattern
            (str "(?s)<" (regexp.QuoteMeta tag)
                 "(?:\\s[^>]*)?>\\s*(.*?)\\s*</"
                 (regexp.QuoteMeta tag) ">"))
           text)
          second
          strings.TrimSpace))

(defn xml-blocks [text tag]
  (map second
       (re-seq
        (re-pattern
         (str "(?s)<" (regexp.QuoteMeta tag)
              "(?:\\s[^>]*)?>(.*?)</"
              (regexp.QuoteMeta tag) ">"))
        text)))

(defn pom-properties [pom]
  (let [parent (first (xml-blocks pom "parent"))
        project (if parent
                  (strings.Replace pom parent "" 1)
                  pom)
        properties (first (xml-blocks pom "properties"))
        property-pairs
        (when properties
          (re-seq
           #"(?s)<([A-Za-z0-9_.-]+)>\s*([^<]*?)\s*</[A-Za-z0-9_.-]+>"
           properties))
        project-group (or (xml-tag project "groupId")
                          (xml-tag parent "groupId"))
        project-version (or (xml-tag project "version")
                            (xml-tag parent "version"))]
    (merge
     {"project.groupId" project-group
      "pom.groupId" project-group
      "project.version" project-version
      "pom.version" project-version}
     (into {}
           (for [[_ key value] property-pairs]
             [key (strings.TrimSpace value)])))))

(defn substitute-properties [value properties]
  (loop [value value
         iteration 0]
    (if (or (nil? value) (>= iteration 10))
      value
      (let [resolved
            (reduce
             (fn [text [key replacement]]
               (if replacement
                 (strings.ReplaceAll
                  text (str "${" key "}") replacement)
                 text))
             value
             properties)]
        (if (= resolved value)
          value
          (recur resolved (inc iteration)))))))

(defn pom-dependency [block properties managed]
  (let [group (substitute-properties
               (xml-tag block "groupId") properties)
        artifact (substitute-properties
                  (xml-tag block "artifactId") properties)
        library (when (and group artifact)
                  (symbol group artifact))
        version (or
                 (substitute-properties
                  (xml-tag block "version") properties)
                 (get managed library))
        scope (or (xml-tag block "scope") "compile")
        optional (xml-tag block "optional")
        type (or (xml-tag block "type") "jar")
        exclusions
        (set
         (keep
          (fn [exclusion]
            (let [group (xml-tag exclusion "groupId")
                  artifact (xml-tag exclusion "artifactId")]
              (when (and group artifact)
                (symbol group artifact))))
          (xml-blocks block "exclusion")))]
    (when (and library version
               (contains? #{"compile" "runtime"} scope)
               (not= "true" optional)
               (= "jar" type))
      [library {:mvn/version version
                :exclusions exclusions}])))

(defn pom-dependencies [pom inherited-exclusions]
  (let [properties (pom-properties pom)
        management-block
        (first (xml-blocks pom "dependencyManagement"))
        managed
        (into {}
              (keep
               (fn [block]
                 (let [group (substitute-properties
                              (xml-tag block "groupId") properties)
                       artifact (substitute-properties
                                 (xml-tag block "artifactId") properties)
                       version (substitute-properties
                                (xml-tag block "version") properties)]
                   (when (and group artifact version)
                     [(symbol group artifact) version])))
               (xml-blocks (or management-block "") "dependency")))
        dependencies-text
        (if management-block
          (strings.Replace pom management-block "" 1)
          pom)]
    (->> (xml-blocks dependencies-text "dependency")
         (keep #(pom-dependency % properties managed))
         (remove #(contains? inherited-exclusions (first %))))))

(defn resolve-maven! [paths seen config library coordinate]
  (ensure-native-resolution! :maven-dependencies)
  (let [version (:mvn/version coordinate)]
    (when-not version
      (throw
       (ex-info
        (str "Maven dependency " library " requires :mvn/version")
        {:gobb/project :invalid-maven-dependency
         :library library})))
    (let [{:keys [relative]} (maven-parts library version)
          repositories (repository-urls config coordinate)
          artifact-root (path:filepath.Join
                         (cache-root) "m2"
                         (path:filepath.FromSlash relative))
          jar (str artifact-root ".jar")
          pom (str artifact-root ".pom")
          extracted (str artifact-root ".d")
          stamp (path:filepath.Join extracted ".gobb-extracted")]
      (download-artifact! repositories relative "jar" jar true)
      (download-artifact! repositories relative "pom" pom false)
      (when-not (exists? stamp)
        (os.MkdirAll extracted 0755)
        (run-command! (str "Extracting " library)
                      ["unzip" "-q" "-o" jar "-d" extracted])
        (os.WriteFile stamp (.getBytes "ok\n") 0644))
      (add-path! paths "." extracted)
      (when (exists? pom)
        (let [inherited-exclusions (set (:exclusions coordinate))]
          (doseq [dependency
                  (pom-dependencies (slurp pom)
                                    inherited-exclusions)]
            (resolve-dependency! paths seen config "."
                                 dependency)))))))

(defn resolve-dependency! [paths seen config root [library coordinate]]
  (let [key [library coordinate root]]
    (when-not (contains? @seen key)
      (swap! seen conj key)
      (cond
        (:local/root coordinate)
        (resolve-local! paths seen root coordinate)

        (or (:git/url coordinate)
            (:git/sha coordinate)
            (:git/tag coordinate)
            (:sha coordinate)
            (:tag coordinate))
        (resolve-git! paths seen root library coordinate)

        (:mvn/version coordinate)
        (resolve-maven! paths seen config library coordinate)

        :else
        (throw
         (ex-info
          (str "Unsupported dependency coordinate for " library
               ": " (pr-str coordinate))
          {:gobb/project :unsupported-coordinate
           :library library
           :coordinate coordinate}))))))

(defn resolve-config! [paths seen config-path config root]
  (let [identity (absolute config-path)]
    (when-not (contains? @seen identity)
      (swap! seen conj identity)
      (doseq [path (:paths config)]
        (add-path! paths root path))
      (when-let [classpath (:classpath config)]
        (doseq [path (path:filepath.SplitList classpath)]
          (when-not (empty? path)
            (add-path! paths root path))))
      (doseq [dependency (:deps config)]
        (resolve-dependency! paths seen config root dependency)))))

(defn merge-config-files [paths]
  (reduce
   (fn [config path]
     (deep-merge config (read-config path)))
   {}
   paths))

(defn configure!
  [{:keys [argv classpath config deps-root aliases merge-deps]}]
  (let [config-paths (discover-configs argv config)
        base-config (merge-config-files config-paths)
        base-config (if merge-deps
                      (deep-merge base-config
                                  (let [value (read-string merge-deps)
                                        task-order
                                        (task-order-from-source merge-deps)]
                                    (when-not (map? value)
                                      (config-error "-Sdeps"
                                                    "expected an EDN map"))
                                    (if task-order
                                      (assoc value task-order-key
                                             task-order)
                                      value)))
                      base-config)
        config (apply-aliases base-config aliases)
        root (absolute
              (or deps-root
                  (some-> config-paths last path:filepath.Dir)
                  "."))
        paths (atom [])
        seen (atom #{})]
    (reset! resolved-config config)
    (warn-minimum-version! config)
    (when-let [primary (last config-paths)]
      (System/setProperty "babashka.config" primary))
    (if (some? classpath)
      (doseq [path (path:filepath.SplitList classpath)]
        (when-not (empty? path)
          (add-path! paths "." path)))
      (resolve-config! paths seen
                       (or (last config-paths)
                           (path:filepath.Join root "deps.edn"))
                       config root))
    (reset! resolved-classpath @paths)
    (reset! configured-classpath
            (if (some? classpath)
              classpath
              (strings.Join @paths
                            (go/string os.PathListSeparator))))
    @paths))

(defn classpath-string []
  (strings.Join @resolved-classpath
                (go/string os.PathListSeparator)))

(defn configured-classpath-string []
  @configured-classpath)

(defn printable-deps []
  (select-keys @resolved-config
               [:paths :deps :aliases :mvn/repos]))

(def source-extensions #{".clj" ".cljc" ".glj"})

(defn stage-tree! [source destination relative]
  (let [[entries error] (os.ReadDir source)]
    (when error
      (throw
       (ex-info
        (str "cannot read classpath directory " source ": " error)
        {:gobb/project :classpath-read-failed
         :path source})))
    (os.MkdirAll destination 0755)
    (doseq [entry entries]
      (let [name (.Name entry)
            from (path:filepath.Join source name)
            relative (if (empty? relative)
                       name
                       (path:filepath.Join relative name))
            source? (contains? source-extensions
                               (path:filepath.Ext name))
            staged-name (if source?
                          (strings.ReplaceAll
                           (path:filepath.ToSlash relative) "/" "__")
                          relative)
            to (path:filepath.Join destination staged-name)]
        (if (.IsDir entry)
          (stage-tree! from destination relative)
          (let [[content read-error] (os.ReadFile from)]
            (when read-error
              (throw
               (ex-info
                (str "cannot read classpath file " from ": " read-error)
                {:gobb/project :classpath-read-failed
                 :path from})))
            (os.MkdirAll (path:filepath.Dir to) 0755)
            (let [write-error (os.WriteFile to content 0644)]
              (when write-error
                (throw
                 (ex-info
                  (str "cannot stage classpath file " to ": "
                       write-error)
                  {:gobb/project :classpath-write-failed
                   :path to}))))))))))

(defn stage-classpath! [destination]
  (doseq [path @resolved-classpath]
    (stage-tree! path destination ""))
  destination)
