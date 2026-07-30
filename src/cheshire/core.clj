(ns cheshire.core
  (:require [clojure.java.io :as io]))

(def default-pretty-print-options
  {:indent-arrays? true
   :indent-objects? true})

(declare to-go)

(defn json-error! [operation error]
  (throw
   (ex-info
    (str "JSON " operation " failed: " (fmt.Sprint error))
    {:gobb/json operation})))

(defn go-map [value]
  (let [result (go/make (go/map-of go/string go/any))]
    (doseq [[key item] value]
      (go/set-map-index
       result
       (cond
         (keyword? key) (name key)
         (symbol? key) (str key)
         :else (str key))
       (to-go item)))
    result))

(defn go-slice [value]
  (apply go/append
         (go/make (go/slice-of go/any) 0)
         (map to-go value)))

(defn to-go [value]
  (cond
    (nil? value) (go/new go/any)
    (map? value) (go-map value)
    (or (sequential? value)
        (set? value)) (go-slice value)
    (keyword? value) (name value)
    (symbol? value) (str value)
    :else value))

(defn json-number? [value]
  (= "json.Number" (str (type value))))

(defn parsed-number [value]
  (read-string (str value)))

(defn map-key [key-fn key]
  (cond
    (true? key-fn) (keyword key)
    (fn? key-fn) (key-fn key)
    :else key))

(defn from-go [value key-fn array-coerce-fn]
  (let [kind (when value (.Kind (reflect.ValueOf value)))]
    (cond
      (nil? value) nil
      (json-number? value) (parsed-number value)
      (= kind reflect.Map)
      (let [reflected (reflect.ValueOf value)]
        (reduce
         (fn [result reflected-key]
           (let [key (.Interface reflected-key)
                 item (.Interface
                       (.MapIndex reflected reflected-key))]
             (assoc result
                    (map-key key-fn key)
                    (from-go item key-fn array-coerce-fn))))
         {}
         (seq (.MapKeys reflected))))
      (or (= kind reflect.Slice)
          (= kind reflect.Array))
      (let [items (mapv #(from-go % key-fn array-coerce-fn)
                        (seq value))]
        (if array-coerce-fn
          (array-coerce-fn items)
          items))
      :else value)))

(defn decode-one [reader key-fn array-coerce-fn]
  (let [decoder (encoding:json.NewDecoder reader)
        target (go/new go/any)]
    (.UseNumber decoder)
    (let [error (.Decode decoder target)]
      (when error
        (json-error! :parse error))
      (from-go (go/deref target) key-fn array-coerce-fn))))

(defn parse-string
  ([string]
   (when string
     (decode-one (strings.NewReader string) nil nil)))
  ([string key-fn]
   (when string
     (decode-one (strings.NewReader string) key-fn nil)))
  ([string key-fn array-coerce-fn]
   (when string
     (decode-one (strings.NewReader string) key-fn array-coerce-fn))))

(defn parse-string-strict
  ([string]
   (parse-string string))
  ([string key-fn]
   (parse-string string key-fn))
  ([string key-fn array-coerce-fn]
   (parse-string string key-fn array-coerce-fn)))

(defn parse-stream
  ([reader]
   (when reader
     (decode-one (io/reader reader) nil nil)))
  ([reader key-fn]
   (when reader
     (decode-one (io/reader reader) key-fn nil)))
  ([reader key-fn array-coerce-fn]
   (when reader
     (decode-one (io/reader reader) key-fn array-coerce-fn))))

(defn parse-stream-strict
  ([reader]
   (parse-stream reader))
  ([reader key-fn]
   (parse-stream reader key-fn))
  ([reader key-fn array-coerce-fn]
   (parse-stream reader key-fn array-coerce-fn)))

(defn generate-string
  ([value]
   (generate-string value nil))
  ([value options]
   (let [[output error]
         (if (:pretty options)
           (encoding:json.MarshalIndent (to-go value) "" "  ")
           (encoding:json.Marshal (to-go value)))]
     (when error
       (json-error! :generate error))
     (go/string output))))

(def encode generate-string)

(defn generate-stream
  ([value writer]
   (generate-stream value writer nil))
  ([value writer options]
   (let [[_ error]
         (io.WriteString writer
                         (generate-string value options))]
     (when error
       (json-error! :generate error))
     nil)))

(def encode-stream generate-stream)
(def decode parse-string)

(defn create-pretty-printer
  ([] default-pretty-print-options)
  ([options] (merge default-pretty-print-options options)))

(defn parsed-seq
  ([reader]
   (parsed-seq reader nil nil))
  ([reader key-fn]
   (parsed-seq reader key-fn nil))
  ([reader key-fn array-coerce-fn]
   (let [decoder (encoding:json.NewDecoder (io/reader reader))]
     (.UseNumber decoder)
     (letfn [(step []
               (lazy-seq
                (let [target (go/new go/any)
                      error (.Decode decoder target)]
                  (cond
                    (nil? error)
                    (cons (from-go (go/deref target)
                                   key-fn
                                   array-coerce-fn)
                          (step))
                    (= "EOF" (fmt.Sprint error)) nil
                    :else (json-error! :parse error)))))]
       (step)))))
