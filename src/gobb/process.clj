(ns babashka.process
  (:require [babashka.tasks :as tasks]
            [gobb.capabilities :as capabilities]
            [gobb.host :as host]))

(def tokenize tasks/tokenize-command)

(def ^:dynamic *defaults*
  {:shutdown nil
   :escape nil
   :program-resolver nil})

(def processes (atom #{}))

(defn normalize-options [options]
  (let [options
        (cond-> options
          (and (:inherit options)
               (not (contains? options :in)))
          (assoc :in :inherit)
          (and (:inherit options)
               (not (contains? options :out)))
          (assoc :out :inherit)
          (and (:inherit options)
               (not (contains? options :err)))
          (assoc :err :inherit))]
    (reduce
     (fn [result stream]
       (let [value (get result stream)]
         (if (or (string? value)
                 (instance? File value)
                 (instance? Path value))
           (-> result
               (assoc stream :write)
               (assoc (keyword (str (name stream) "-file"))
                      value))
           result)))
     options
     [:out :err])))

(defn parse-args [arguments]
  (let [maybe-previous (first arguments)
        [previous arguments]
        (if (or (contains? @processes maybe-previous)
                (and (map? maybe-previous)
                     (contains? maybe-previous :parsed)))
          [maybe-previous (rest arguments)]
          [nil arguments])
        maybe-options (first arguments)
        [options arguments]
        (if (map? maybe-options)
          [maybe-options (rest arguments)]
          [nil arguments])
        arguments
        (if (and (= 1 (count arguments))
                 (sequential? (first arguments)))
          (first arguments)
          arguments)
        arguments (mapv str arguments)
        arguments
        (if (and (= 1 (count arguments))
                 (string? (first arguments)))
          (tokenize (first arguments))
          arguments)]
    {:prev (or (:prev options) previous)
     :cmd (or (:cmd options) arguments)
     :opts (dissoc (or options {}) :prev :cmd)}))

(defn process* [{:keys [prev cmd opts]}]
  (capabilities/require! :process :spawn)
  (let [previous-result (when prev @prev)
        options
        (cond-> (normalize-options
                 (merge *defaults*
                        {:out :string
                         :err :string}
                        opts
                        {:continue true}))
          (and previous-result
               (not (contains? opts :in))
               (string? (:out previous-result)))
          (assoc :in (:out previous-result)))
        _ (when-let [pre-start-fn (:pre-start-fn options)]
            (pre-start-fn {:cmd cmd}))
        result
        (future
          (let [completed
                (assoc (apply tasks/shell options cmd)
                       :prev prev
                       :cmd cmd)]
            (when-let [exit-fn (:exit-fn options)]
              (exit-fn completed))
            completed))]
    (swap! processes conj result)
    (when-let [shutdown (:shutdown options)]
      (when (fn? shutdown)
        (host/register-shutdown-hook!
         #(shutdown result))))
    result))

(defn process [& arguments]
  (process* (parse-args arguments)))

(defn pb [& arguments]
  {:babashka.process/builder true
   :parsed (parse-args arguments)})

(defn start [builder]
  (process* (:parsed builder)))

(defn pipeline
  ([process]
   (if-let [previous (:prev @process)]
     (conj (pipeline previous) process)
     [process]))
  ([builder & builders]
   (let [builders (cons builder builders)]
     (loop [remaining builders
            previous nil
            result []]
       (if-let [builder (first remaining)]
         (let [parsed (cond-> (:parsed builder)
                        previous (assoc :prev previous))
               process (process* parsed)]
           (recur (next remaining)
                  process
                  (conj result process)))
         result)))))

(defn check [process]
  (let [process (if (contains? @processes process)
                  @process
                  process)]
    (if (zero? (:exit process))
      process
      (throw
       (ex-info
        (or (not-empty (:err process)) "failed")
        (assoc process :type ::error))))))

(defn sh [& arguments]
  (let [{:keys [opts] :as parsed} (parse-args arguments)]
    @(process* (assoc parsed
                      :opts (merge {:out :string
                                    :err :string}
                                   opts)))))

(defn shell [& arguments]
  (let [{:keys [opts] :as parsed} (parse-args arguments)
        process
        (process*
         (assoc parsed
                :opts
                (merge {:in :inherit
                        :out :inherit
                        :err :inherit}
                       opts)))
        result @process]
    (if (:continue opts)
      result
      (check process))))

(defn process-unquote [argument]
  (if (and (seq? argument)
           (= 'unquote (first argument)))
    (second argument)
    argument))

(defn format-argument [argument]
  (if (seq? argument)
    (process-unquote argument)
    (list 'quote argument)))

(defmacro $
  [& arguments]
  (let [options? (map? (first arguments))
        options (when options? (first arguments))
        command (mapv format-argument
                      (if options?
                        (rest arguments)
                        arguments))]
    (if options?
      (list 'babashka.process/process options command)
      (list 'babashka.process/process command))))

(defn destroy [process]
  process)

(def destroy-tree destroy)

(defn alive? [process]
  (and (contains? @processes process)
       (not (realized? process))))

(defn exec [& _]
  (capabilities/unsupported! :process :replace-image))
