(ns cognitect.transit
  (:refer-clojure :exclude [read])
  (:require [cheshire.core :as json]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(defn writer
  ([output type]
   (writer output type nil))
  ([output type options]
   (when-not (contains? #{:json :json-verbose} type)
     (throw
      (ex-info
       (str "Unsupported Transit writer type: " type)
       {:type type})))
   {:output output
    :type type
    :options options
    :first? (atom true)}))

(defn scalar-key? [value]
  (or (string? value)
      (keyword? value)
      (symbol? value)
      (number? value)
      (boolean? value)))

(defn escape-string [value]
  (if (and (seq value)
           (contains? #{\~ \^ \`} (first value)))
    (str "~" value)
    value))

(declare encode-value)

(defn encode-map [value]
  (into ["^ "]
        (mapcat (fn [[key item]]
                  [(encode-value key)
                   (encode-value item)]))
        value))

(defn encode-value [value]
  (cond
    (nil? value) nil
    (string? value) (escape-string value)
    (keyword? value) (str "~:" (subs (str value) 1))
    (symbol? value) (str "~$" value)
    (map? value) (encode-map value)
    (vector? value) (mapv encode-value value)
    (set? value) ["~#set" (mapv encode-value value)]
    (list? value) ["~#list" (mapv encode-value value)]
    (sequential? value) (mapv encode-value value)
    :else value))

(defn quoted-top-level [value]
  (if (scalar-key? value)
    ["~#'" (encode-value value)]
    (encode-value value)))

(defn write [writer value]
  (when-not @(:first? writer)
    (let [[_ error] (io.WriteString (:output writer) " ")]
      (when error
        (throw error))))
  (reset! (:first? writer) false)
  (let [[_ error]
        (io.WriteString (:output writer)
                        (json/generate-string
                         (quoted-top-level value)))]
    (when error
      (throw error))
    nil))

(defn reader
  ([input type]
   (reader input type nil))
  ([input type options]
   (when-not (contains? #{:json :json-verbose} type)
     (throw
      (ex-info
       (str "Unsupported Transit reader type: " type)
       {:type type})))
   (let [[content error] (io.ReadAll (io/input-stream input))]
     (when error
       (throw error))
     {:values
      (atom
       (seq
        (json/parsed-seq
         (strings.NewReader (go/string content)))))
      :type type
      :options options})))

(declare decode-value)

(defn decode-map [value]
  (loop [entries (next value)
         result {}]
    (if (seq entries)
      (recur (nnext entries)
             (assoc result
                    (decode-value (first entries))
                    (decode-value (second entries))))
      result)))

(defn decode-string [value]
  (cond
    (str/starts-with? value "~~") (subs value 1)
    (str/starts-with? value "~^") (subs value 1)
    (str/starts-with? value "~`") (subs value 1)
    (str/starts-with? value "~:") (keyword (subs value 2))
    (str/starts-with? value "~$") (symbol (subs value 2))
    :else value))

(defn decode-value [value]
  (cond
    (string? value) (decode-string value)
    (and (vector? value)
         (= "~#'" (first value)))
    (decode-value (second value))
    (and (vector? value)
         (= "^ " (first value)))
    (decode-map value)
    (and (vector? value)
         (= "~#set" (first value)))
    (set (map decode-value (second value)))
    (and (vector? value)
         (= "~#list" (first value)))
    (apply list (map decode-value (second value)))
    (vector? value) (mapv decode-value value)
    :else value))

(defn read [reader]
  (let [values @(:values reader)]
    (when-not (seq values)
      (throw
       (ex-info "No more Transit values" {:type :eof})))
    (let [value (first values)]
      (swap! (:values reader) next)
      (decode-value value))))
