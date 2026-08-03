(ns gobb.host
  (:require [gobb.capabilities :as capabilities]))

(def no-source-path "NO_SOURCE_PATH")
(def repl-source-path "<repl>")
(def preload-source-path "<preloads>")

(def load-paths (atom []))
(def configured-data-readers (atom {}))
(def configured-default-data-reader (atom nil))
(def shutdown-hooks (atom []))
(def shutdown-ran? (atom false))
(def current-source (atom no-source-path))

(def core-load (atom nil))
(def core-assoc (atom nil))
(def core-destructure (atom nil))
(def core-fn-macro (atom nil))
(def core-name (atom nil))
(def core-remove-ns (atom nil))
(def initialized? (atom false))

(defn load-clojure-test! []
  ;; Referencing Glojure's precompiled stdlib package links its namespace
  ;; loaders into Gobb without recompiling clojure.test from source. Load the
  ;; protocol roots and template macros first because clojure.test reduces over
  ;; test Vars and its `are` macro expands through clojure.template.
  (github.com:glojurelang:glojure:pkg:stdlib:clojure:core:protocols.LoadNS)
  (github.com:glojurelang:glojure:pkg:stdlib:clojure:walk.LoadNS)
  (github.com:glojurelang:glojure:pkg:stdlib:clojure:template.LoadNS)
  (github.com:glojurelang:glojure:pkg:stdlib:clojure:test.LoadNS))

(defn runtime-import-one! [qualified-name]
  (let [separator (strings.LastIndex qualified-name ".")
        package-name (subs qualified-name 0 separator)
        class-name (subs qualified-name (inc separator))
        [host-value found?]
        (github.com:glojurelang:glojure:pkg:pkgmap.Get qualified-name)
        [host-class class-found?]
        (github.com:glojurelang:glojure:pkg:pkgmap.HostClass class-name)
        source-ns (when-not (or found? class-found?)
                    (find-ns (symbol package-name)))
        source-var (when source-ns
                     (ns-resolve source-ns (symbol class-name)))
        value (cond
                found? host-value
                class-found? host-class
                source-var @source-var)]
    (when value
      (.Import *ns* qualified-name value))))

(defmacro runtime-import [& import-symbols-or-lists]
  ;; Runtime-loaded namespaces can import both registered JVM compatibility
  ;; classes and record descriptors defined by another loaded namespace.
  (let [qualified-names
        (for [raw-spec import-symbols-or-lists
              :let [spec (if (and (seq? raw-spec)
                                  (= 'quote (first raw-spec)))
                           (second raw-spec)
                           raw-spec)]
              qualified-name
              (if (symbol? spec)
                [(str spec)]
                (map (fn [class-name]
                       (str (first spec) "." class-name))
                     (rest spec)))]
          qualified-name)]
    (cons 'do
          (map (fn [qualified-name]
                 (list 'gobb.host/runtime-import-one! qualified-name))
               qualified-names))))

(defn register-runtime-host-forms! []
  ;; These constructors are emitted by macros in dynamically loaded library
  ;; source. Register them explicitly because Gloat cannot discover host forms
  ;; that appear only after runtime macro expansion.
  (github.com:glojurelang:glojure:pkg:javacompat:base64.Link)
  (github.com:glojurelang:glojure:pkg:javacompat:nio.Link)
  (github.com:glojurelang:glojure:pkg:pkgmap.Set
   "github.com/glojurelang/glojure/pkg/lang.NewMultiFn"
   github.com:glojurelang:glojure:pkg:lang.NewMultiFn)
  (github.com:glojurelang:glojure:pkg:pkgmap.Set
   "github.com/glojurelang/glojure/pkg/lang.NewDelay"
   github.com:glojurelang:glojure:pkg:lang.NewDelay)
  (github.com:glojurelang:glojure:pkg:pkgmap.Set
   "github.com/glojurelang/glojure/pkg/lang.NewLazySeq"
   github.com:glojurelang:glojure:pkg:lang.NewLazySeq)
  (github.com:glojurelang:glojure:pkg:pkgmap.Set
   "github.com/glojurelang/glojure/pkg/lang.Identical"
   github.com:glojurelang:glojure:pkg:lang.Identical)
  (github.com:glojurelang:glojure:pkg:pkgmap.Set
   "github.com/glojurelang/glojure/pkg/lang.NewPersistentArrayMapAsIfByAssoc"
   github.com:glojurelang:glojure:pkg:lang.NewPersistentArrayMapAsIfByAssoc)
  (github.com:glojurelang:glojure:pkg:lang.RegisterHostConstructor
   "clojure.lang.MapEntry"
   (fn [key value]
     (github.com:glojurelang:glojure:pkg:lang.NewMapEntry key value)))
  (let [java-name "clojure.lang.PersistentArrayMap"
        class (github.com:glojurelang:glojure:pkg:lang.NewClass
               (reflect.TypeOf {}) java-name)]
    (github.com:glojurelang:glojure:pkg:pkgmap.SetHostClassPackage
     "PersistentArrayMap" "clojure.lang")
    (github.com:glojurelang:glojure:pkg:pkgmap.SetHostClass
     "PersistentArrayMap" class))
  (let [java-name "clojure.lang.PersistentHashMap"
        sample (into {} (map (fn [value] [value value]) (range 50)))
        class (github.com:glojurelang:glojure:pkg:lang.NewClass
               (reflect.TypeOf sample) java-name)]
    (github.com:glojurelang:glojure:pkg:pkgmap.SetHostClassPackage
     "PersistentHashMap" "clojure.lang")
    (github.com:glojurelang:glojure:pkg:pkgmap.SetHostClass
     "PersistentHashMap" class))
  (github.com:glojurelang:glojure:pkg:pkgmap.Set
   "clojure.lang.RT.chunkIteratorSeq"
   (fn [iter] (seq iter)))
  (github.com:glojurelang:glojure:pkg:pkgmap.Set
   "clojure.lang.RT.iter"
   identity)
  (github.com:glojurelang:glojure:pkg:pkgmap.Set
   "clojure.lang.TransformerIterator.create"
   (fn [xform iter]
     (seq (transduce xform conj [] iter))))
  (github.com:glojurelang:glojure:pkg:pkgmap.Set
   "strings.Builder"
   strings.Builder)
  ;; Class.forName is primarily used by portable sources to identify JVM
  ;; primitive array classes. Return the hosted Go type for byte[].
  (doseq [class-name ["Class" "java.lang.Class"]]
    (github.com:glojurelang:glojure:pkg:pkgmap.Set
     (str class-name ".forName")
     (fn [name]
       (case name
         "[B" (reflect.TypeOf (go/make (go/slice-of go/byte) 0))
         (throw (errors.New (str "Class.forName: unsupported class " name)))))))
  ;; Locale arguments only select Unicode case conversion behavior for the
  ;; portable libraries Gobb currently hosts. Glojure's string bridge already
  ;; performs that conversion and deliberately ignores extra arguments.
  (github.com:glojurelang:glojure:pkg:pkgmap.SetHostClassPackage
   "Locale" "java.util")
  (github.com:glojurelang:glojure:pkg:pkgmap.SetHostClass
   "Locale" (reflect.TypeOf ""))
  (doseq [class-name ["Locale" "java.util.Locale"]]
    (github.com:glojurelang:glojure:pkg:pkgmap.Set
     (str class-name ".US") (fn [] "en-US"))
    (github.com:glojurelang:glojure:pkg:pkgmap.Set
     (str class-name ".getDefault") (fn [] "en-US"))
    (github.com:glojurelang:glojure:pkg:pkgmap.Set
     (str class-name ".forLanguageTag") (fn [tag] tag))
    (github.com:glojurelang:glojure:pkg:pkgmap.Set
     (str class-name ".setDefault") (fn [_] nil)))
  ;; Aero only needs the line-numbering reader's pushback behavior on its
  ;; successful parse path. Reuse Glojure's Java-compatible PushbackReader;
  ;; its missing getLineNumber method is relevant only while formatting a
  ;; malformed configuration error.
  (let [java-name "clojure.lang.LineNumberingPushbackReader"
        sample (github.com:glojurelang:glojure:pkg:javacompat:streams.NewPushbackReader
                (strings.NewReader ""))
        class (github.com:glojurelang:glojure:pkg:lang.NewClass
               (reflect.TypeOf sample) java-name)]
    (github.com:glojurelang:glojure:pkg:pkgmap.SetHostClassPackage
     "LineNumberingPushbackReader" "clojure.lang")
    (github.com:glojurelang:glojure:pkg:pkgmap.SetHostClass
     "LineNumberingPushbackReader" class)
    (github.com:glojurelang:glojure:pkg:lang.RegisterHostConstructor
     java-name
     (fn [& args]
       (apply
        github.com:glojurelang:glojure:pkg:javacompat:streams.NewPushbackReader
        args))))
  (let [java-name "java.io.EOFException"
        class (github.com:glojurelang:glojure:pkg:lang.NewClass
               (reflect.TypeOf (errors.New "")) java-name)]
    (github.com:glojurelang:glojure:pkg:pkgmap.SetHostClassPackage
     "EOFException" "java.io")
    (github.com:glojurelang:glojure:pkg:pkgmap.SetHostClass
     "EOFException" class)
    (github.com:glojurelang:glojure:pkg:lang.RegisterHostConstructor
     java-name (fn [message] (errors.New message))))
  (let [java-name "java.io.IOException"
        class (github.com:glojurelang:glojure:pkg:lang.NewClass
               (reflect.TypeOf (errors.New "")) java-name)]
    (github.com:glojurelang:glojure:pkg:pkgmap.SetHostClassPackage
     "IOException" "java.io")
    (github.com:glojurelang:glojure:pkg:pkgmap.SetHostClass
     "IOException" class)
    (github.com:glojurelang:glojure:pkg:lang.RegisterHostConstructor
     java-name (fn [message] (errors.New message))))
  ;; Portable retry libraries use this class in catch clauses even when no
  ;; interruption is raised. Give the imported JVM name a distinct hosted
  ;; error class so those clauses compile without broadening ordinary catches.
  (let [java-name "java.lang.InterruptedException"
        class (github.com:glojurelang:glojure:pkg:lang.NewClass
               (reflect.TypeOf (errors.New "")) java-name)]
    (github.com:glojurelang:glojure:pkg:pkgmap.SetHostClassPackage
     "InterruptedException" "java.lang")
    (github.com:glojurelang:glojure:pkg:pkgmap.SetHostClass
     "InterruptedException" class)
    (github.com:glojurelang:glojure:pkg:lang.RegisterHostConstructor
     java-name (fn [message] (errors.New message))))
  (let [java-name "java.text.ParseException"
        class (github.com:glojurelang:glojure:pkg:lang.NewClass
               (reflect.TypeOf (errors.New "")) java-name)]
    (github.com:glojurelang:glojure:pkg:pkgmap.SetHostClassPackage
     "ParseException" "java.text")
    (github.com:glojurelang:glojure:pkg:pkgmap.SetHostClass
     "ParseException" class)
    (github.com:glojurelang:glojure:pkg:lang.RegisterHostConstructor
     java-name (fn [message & _] (errors.New message)))))

(defn spit* [file content & options]
  ;; Glojure's clojure.core/spit currently targets the unimplemented
  ;; glojure.go.io/writer host form. Route it through Gobb's Java I/O
  ;; compatibility namespace instead.
  (require 'clojure.java.io)
  (let [writer-fn (ns-resolve 'clojure.java.io 'writer)
        writer (apply writer-fn file options)]
    (try
      (let [[_ error] (io.WriteString writer (str content))]
        (when error
          (throw error))
        nil)
      (finally
        (.Close writer)))))

(defn slurp* [file & options]
  ;; The precompiled clojure.core/slurp dispatch table does not know Gobb's
  ;; Java-compatible File wrapper. Use the same reader bridge as
  ;; clojure.java.io and consume its Go io.Reader directly.
  (require 'clojure.java.io)
  (let [reader-fn (ns-resolve 'clojure.java.io 'reader)
        reader (apply reader-fn file options)]
    (try
      (let [[content error] (io.ReadAll reader)]
        (when error (throw error))
        (go/string content))
      (finally
        (.Close reader)))))

(defn uuid?* [value]
  ;; java.util.UUID compatibility values are backed by gojava rather than the
  ;; google UUID value used by Glojure's built-in random-uuid implementation.
  (github.com:glojurelang:glojure:pkg:javacompat:uuid.IsUUID value))

(defn flush* []
  ;; Gobb's standard streams and strings.Builder writes are immediate; unlike
  ;; a JVM Writer they do not expose a Sync method.
  nil)

(defn read*
  ([] (read* *in*))
  ([stream] (read* stream true nil false))
  ([stream eof-error? eof-value] (read* stream eof-error? eof-value false))
  ([stream eof-error? eof-value _recursive?]
   (let [reader (github.com:glojurelang:glojure:pkg:reader.New stream)
         [value error] (.ReadOne reader)]
     (cond
       (nil? error) value
       (and (or (errors.Is error io.EOF)
                (errors.Is error github.com:glojurelang:glojure:pkg:reader.ErrEOF))
            (not eof-error?)) eof-value
       :else (throw error))))
  ([options stream]
   (binding [*data-readers* (merge *data-readers* (:readers options))
             *default-data-reader-fn* (:default options)]
     (read* stream
            (not (contains? options :eof))
            (:eof options)
            false))))

(defn take*
  ;; Clojure treats positive infinity as an unbounded take. The generated
  ;; Glojure core function narrows its numeric argument before dispatch, so
  ;; preserve the lazy collection directly for this Babashka-supported case.
  ([n]
   (fn [rf]
     (let [remaining (volatile! n)]
       (fn
         ([] (rf))
         ([result] (rf result))
         ([result input]
          (let [n @remaining
                next-n (vswap! remaining dec)
                result (if (pos? n) (rf result input) result)]
            (if (not (pos? next-n)) (ensure-reduced result) result)))))))
  ([n collection]
   (if (= ##Inf n)
     collection
     (lazy-seq
      (when (pos? n)
        (when-let [items (seq collection)]
          (cons (first items) (take* (dec n) (rest items)))))))))

(defn reduce-kv* [f init collection]
  ;; Glojure's precompiled protocol table predates some hosted collection
  ;; implementations. Use their public collection semantics directly so newly
  ;; linked maps and vectors participate without rebuilding the standard lib.
  (cond
    (map? collection)
    (loop [result init
           entries (seq collection)]
      (if entries
        (let [entry (first entries)
              next-result (f result (key entry) (val entry))]
          (if (reduced? next-result)
            @next-result
            (recur next-result (next entries))))
        result))

    (vector? collection)
    (loop [result init
           index 0]
      (if (< index (count collection))
        (let [next-result (f result index (nth collection index))]
          (if (reduced? next-result)
            @next-result
            (recur next-result (inc index))))
        result))

    :else
    (clojure.core.protocols/kv-reduce collection f init)))

(defn rseq* [collection]
  ;; The precompiled core protocol table does not include hosted SubVector.
  ;; Build the reverse sequence through its public indexed interface.
  (when (pos? (count collection))
    (map #(nth collection %)
         (range (dec (count collection)) -1 -1))))

(defn satisfies?* [protocol value]
  ;; The current Glojure core does not expose satisfies?. Cover the hosted
  ;; IKVReduce protocol used by BB-targeted transducer libraries; Gobb's
  ;; reduce-kv bridge above supports these same collection categories.
  (if (identical? protocol clojure.core.protocols/IKVReduce)
    (or (map? value) (vector? value))
    false))

(defn get-method* [multifn dispatch-value]
  ;; Glojure exposes MultiFn's method table, while its current get-method
  ;; wrapper targets an unexported Go method. Exact and default dispatch cover
  ;; the public Clojure API and the clojure.test report extension contract.
  (let [method-table (methods multifn)]
    (or (get method-table dispatch-value)
        (get method-table :default))))

(defn assoc*
  ([collection key value]
   ;; Glojure's map node identity check currently compares function-wrapper
   ;; structs directly when replacing a function value. Remove that entry
   ;; first so ordinary persistent-map association remains safe.
   (@core-assoc
    (if (and (map? collection)
             (contains? collection key)
             (fn? value))
      (dissoc collection key)
      collection)
    key value))
  ([collection key value & key-values]
   (loop [result (assoc* collection key value)
          entries key-values]
     (if (seq entries)
       (recur (assoc* result (first entries) (second entries))
              (nnext entries))
       result))))

(defn name* [value]
  ;; The hosted keyword reader represents :/ with empty name/namespace
  ;; components. Preserve Clojure's public name contract for this operator.
  (if (= :/ value)
    "/"
    (@core-name value)))

(defn load* [& paths]
  ;; Glojure's AOT-compiled clojure.core/load currently specializes *ns* to
  ;; clojure.core. Resolve relative resources here before delegating so a
  ;; namespace such as clojure.pprint loads pprint/utilities from
  ;; /clojure/pprint/utilities rather than /pprint/utilities.
  (let [current-ns (var-get #'*ns*)
        resource (-> (str (ns-name current-ns))
                     (strings.ReplaceAll "-" "_")
                     (strings.ReplaceAll "." "/"))
        separator (strings.LastIndex resource "/")
        root (if (neg? separator)
               ""
               (subs resource 0 separator))]
    (apply @core-load
           (map (fn [path]
                  (if (strings.HasPrefix path "/")
                    path
                    (str "/" root
                         (when (seq root) "/")
                         path)))
                paths))))

(defn normalize-binding-form [form]
  (cond
    (vector? form)
    (mapv normalize-binding-form form)

    (map? form)
    (reduce-kv
     (fn [result key value]
       (assoc result key
              (if (and (= :keys key) (vector? value))
                (mapv (fn [binding]
                        (if (keyword? binding)
                          (symbol (namespace binding) (name binding))
                          binding))
                      value)
                (normalize-binding-form value))))
     {}
     form)

    :else form))

(defn destructure* [bindings]
  (@core-destructure (normalize-binding-form bindings)))

(defn let-macro* [_form _environment bindings & body]
  (list* 'let* (destructure* bindings) body))

(defn normalize-fn-signature [signature]
  (if (vector? signature)
    (normalize-binding-form signature)
    (with-meta
      (cons (normalize-binding-form (first signature))
            (next signature))
      (meta signature))))

(defn fn-macro* [form environment & signatures]
  (let [function-name (when (symbol? (first signatures))
                        (first signatures))
        signatures (if function-name (next signatures) signatures)
        normalized (if (vector? (first signatures))
                     (cons (normalize-binding-form (first signatures))
                           (next signatures))
                     (map normalize-fn-signature signatures))
        normalized (if function-name
                     (cons function-name normalized)
                     normalized)]
    (apply @core-fn-macro form environment normalized)))

(defn remove-ns* [namespace-name]
  ;; Babashka can require a namespace immediately after removing it. Glojure's
  ;; loaded-lib registry otherwise suppresses that reload and leaves callers
  ;; with no namespace to resolve.
  (dosync
   (commute @#'clojure.core/*loaded-libs* disj namespace-name))
  (@core-remove-ns namespace-name))

(defn initialize-runtime! []
  ;; Babashka reads shared .cljc sources as both Clojure and Babashka. Keep
  ;; Glojure's default :glj feature and opt this embedding into :clj and :bb.
  (github.com:glojurelang:glojure:pkg:reader.EnableFeature "clj")
  (github.com:glojurelang:glojure:pkg:reader.EnableFeature "bb")
  (register-runtime-host-forms!)
  ;; Runtime-loaded portable libraries inspect this standard Clojure var when
  ;; selecting reader and compatibility behavior. Glojure does not currently
  ;; predeclare it in its hosted core namespace.
  (when-not (ns-resolve 'clojure.core '*clojure-version*)
    (intern 'clojure.core '*clojure-version*
            {:major 1 :minor 12 :incremental 4 :qualifier nil}))
  (when-not (ns-resolve 'clojure.core 'default-data-readers)
    (intern 'clojure.core 'default-data-readers
            {'inst github.com:glojurelang:glojure:pkg:javacompat:date.ParseInstantDate
             'uuid github.com:glojurelang:glojure:pkg:javacompat:uuid.FromString}))
  (when-not (ns-resolve 'clojure.core 'satisfies?)
    (intern 'clojure.core 'satisfies? satisfies?*))
  (alter-var-root #'clojure.core/get-method (constantly get-method*))
  ;; Glojure initializes *in* and *out* at bootstrap, but its generated
  ;; clojure.core currently leaves *err* nil. Own all three roots here so the
  ;; Gobb execution host has one explicit standard-stream contract.
  (alter-var-root #'*in* (constantly os.Stdin))
  (alter-var-root #'*out* (constantly os.Stdout))
  (alter-var-root #'*err* (constantly os.Stderr))
  (alter-var-root #'clojure.core/flush (constantly flush*))
  (alter-var-root #'clojure.core/read (constantly read*))
  (alter-var-root #'clojure.core/reduce-kv (constantly reduce-kv*))
  (alter-var-root #'clojure.core/rseq (constantly rseq*))
  (reset! core-assoc @#'clojure.core/assoc)
  (alter-var-root #'clojure.core/assoc (constantly assoc*))
  (reset! core-destructure @#'clojure.core/destructure)
  (alter-var-root #'clojure.core/destructure (constantly destructure*))
  (alter-var-root #'clojure.core/let (constantly let-macro*))
  (reset! core-fn-macro @#'clojure.core/fn)
  (alter-var-root #'clojure.core/fn (constantly fn-macro*))
  (reset! core-name @#'clojure.core/name)
  (alter-var-root #'clojure.core/name (constantly name*))
  (reset! core-remove-ns @#'clojure.core/remove-ns)
  (alter-var-root #'clojure.core/remove-ns (constantly remove-ns*))
  (reset! core-load @#'clojure.core/load)
  (alter-var-root #'clojure.core/load (constantly load*))
  (alter-var-root #'clojure.core/take (constantly take*))
  (alter-var-root #'clojure.core/uuid? (constantly uuid?*))
  (alter-var-root #'clojure.core/slurp (constantly slurp*))
  (alter-var-root #'clojure.core/spit (constantly spit*))
  (alter-var-root #'clojure.core/import (constantly @#'runtime-import))
  (load-clojure-test!)
  ;; Runtime codegen can classify a fully-qualified protocol Var as a late
  ;; host form before its namespace alias table is complete.
  (github.com:glojurelang:glojure:pkg:pkgmap.Set
   "clojure.core.protocols.IKVReduce"
   (var-get (ns-resolve 'clojure.core.protocols 'IKVReduce)))
  (in-ns 'user)
  (refer 'clojure.core))

(defn initialize! []
  ;; The CLI initializes the host before dispatching, while reusable entry
  ;; points such as the browser REPL initialize themselves. Runtime patches
  ;; must only capture and replace core Vars once.
  (when (compare-and-set! initialized? false true)
    (initialize-runtime!)))

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
                  (or (capabilities/environment
                       "BABASHKA_PRELOADS") ""))]
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
  (let [source (-> source
                   rewrite-reader-features
                   rewrite-tagged-literals)
        read-and-evaluate
        (fn []
          ;; Read all top-level forms as data, then evaluate them one at a
          ;; time. This lets an earlier require, ns, or defmacro affect the
          ;; analysis of every form that follows it.
          (let [forms (read-string (str "[\n" source "\n]"))
                result
                (reduce (fn [_ form] (eval form))
                        nil
                        forms)]
            (when (and print-result? (some? result))
              (prn result))
            result))]
    (binding [*file* file]
      (read-and-evaluate))))
