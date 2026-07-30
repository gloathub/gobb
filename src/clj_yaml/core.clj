(ns clj-yaml.core
  (:require [cheshire.core :as json]
            [clojure.java.io :as io]))

(defrecord Marked [start end unmark])

(defn marked? [value]
  (instance? Marked value))

(defn unmark [value]
  (if (marked? value)
    (:unmark value)
    value))

(defn yaml-error! [operation error]
  (throw
   (ex-info
    (str "YAML " operation " failed: " (fmt.Sprint error))
    {:gobb/yaml operation})))

(defn option-map [options]
  (if (and (= 1 (count options))
           (map? (first options)))
    (first options)
    (apply hash-map options)))

(defn key-converter [options]
  (cond
    (:key-fn options)
    (fn [key] ((:key-fn options) {:key key}))

    (not= false (:keywords options true))
    true

    :else nil))

(defn decode-one [decoder options]
  (let [target (go/new go/any)
        error (.Decode decoder target)]
    (cond
      (nil? error)
      {:value (json/from-go
               (go/deref target)
               (key-converter options)
               nil)}

      (= "EOF" (fmt.Sprint error))
      {:eof true}

      :else
      (yaml-error! :parse error))))

(defn decode-stream [reader options]
  (let [decoder (gopkg.in:yaml.v3.NewDecoder reader)]
    (if (:load-all options)
      (loop [documents []]
        (let [{:keys [value eof]} (decode-one decoder options)]
          (if eof
            documents
            (recur (conj documents value)))))
      (:value (decode-one decoder options)))))

(defn parse-string [yaml-string & options]
  (decode-stream
   (strings.NewReader yaml-string)
   (option-map options)))

(defn parse-stream [reader & options]
  (decode-stream
   (io/reader reader)
   (option-map options)))

(defn configure-encoder [encoder options]
  (when-let [indent (get-in options [:dumper-options :indent])]
    (.SetIndent encoder indent))
  encoder)

(defn generate-stream [writer data & options]
  (let [options (option-map options)
        encoder (configure-encoder
                 (gopkg.in:yaml.v3.NewEncoder writer)
                 options)
        error (.Encode encoder (json/to-go data))]
    (when error
      (yaml-error! :generate error))
    nil))

(defn generate-string [data & options]
  (let [output (StringWriter.)]
    (apply generate-stream output data options)
    (str output)))
