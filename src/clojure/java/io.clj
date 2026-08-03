(ns clojure.java.io
  (:require [gobb.capabilities :as capabilities]
            [gobb.host :as host]))

(defn as-file [value]
  (if (instance? File value)
    value
    (File. (str value))))

(defn file
  ([value]
   (as-file value))
  ([parent child]
   (File. (as-file parent) (str child)))
  ([parent child & more]
   (reduce file (file parent child) more)))

(defn as-relative-path [value]
  (let [value (str value)]
    (when (path:filepath.IsAbs value)
      (throw
       (IllegalArgumentException.
        (str value " is not a relative path"))))
    value))

(defn as-url [value]
  (cond
    (instance? File value)
    (str "file://" (.GetAbsolutePath value))

    (instance? Path value)
    (str "file://" (str (.ToAbsolutePath value)))

    :else (str value)))

(defn byte-array-value? [value]
  (contains? #{"[]int8" "[]uint8"}
             (str (type value))))

(defn input-stream
  [value & _options]
  (capabilities/require! :filesystem :read)
  (cond
    (instance? InputStream value) value
    (instance? Reader value) value
    (or (instance? File value)
        (instance? Path value)
        (string? value))
    (FileInputStream. (str value))
    (byte-array-value? value)
    (ByteArrayInputStream. (vec value))
    (sequential? value)
    (ByteArrayInputStream. value)
    :else
    (throw
     (IllegalArgumentException.
     (str "Cannot open " (type value)
           " as an InputStream")))))

(defn option-map [options]
  (if (and (= 1 (count options))
           (map? (first options)))
    (first options)
    (apply hash-map options)))

(defn output-stream
  [value & options]
  (capabilities/require! :filesystem :write)
  (let [options (option-map options)
        append (:append options)]
    (cond
      (instance? OutputStream value) value
      (instance? Writer value) value
      (or (instance? File value)
          (instance? Path value)
          (string? value))
      (FileOutputStream. (str value) append)
      :else
      (throw
       (IllegalArgumentException.
        (str "Cannot open " (type value)
             " as an OutputStream"))))))

(defn reader
  [value & options]
  (cond
    (instance? Reader value) value
    ;; java.lang.String/toCharArray is represented as a hosted sequence of
    ;; characters. Preserve java.io's char-array reader behavior instead of
    ;; treating those code points as a byte input stream.
    (and (sequential? value) (every? char? value))
    (StringReader. (apply str value))
    :else (apply input-stream value options)))

(defn writer
  [value & options]
  (if (instance? Writer value)
    value
    (apply output-stream value options)))

(defn copy-input [value]
  (cond
    (string? value) (StringReader. value)
    (byte-array-value? value)
    (ByteArrayInputStream. (vec value))
    (sequential? value)
    (ByteArrayInputStream. value)
    (or (instance? InputStream value)
        (instance? Reader value)) value
    :else (input-stream value)))

(defn copy-output [value options]
  (if (or (instance? OutputStream value)
          (instance? Writer value))
    value
    (apply output-stream value options)))

(defn copy
  [input output & options]
  (let [close-input
        (or (instance? File input)
            (instance? Path input))
        close-output
        (not (or (instance? OutputStream output)
                 (instance? Writer output)))
        input (copy-input input)
        output (copy-output output options)
        [written error] (io.Copy output input)]
    (when close-input (.Close input))
    (when close-output (.Close output))
    (when error
      (throw
       (ex-info
        (str "I/O copy failed: " (fmt.Sprint error))
        {:gobb/io :copy})))
    nil))

(defn make-parents [value]
  (let [target (file value)
        parent (.GetParent target)]
    (when (and parent
               (not= "." parent)
               (not= parent (str target)))
      (let [error (os.MkdirAll parent 0777)]
        (when error
          (throw
           (ex-info
            (str "Could not create parent directories: "
                 (fmt.Sprint error))
            {:gobb/io :make-parents
             :path parent})))))
    true))

(defn delete-file
  ([value]
   (delete-file value false))
  ([value silently]
   (capabilities/require! :filesystem :remove)
   (let [error (os.Remove (str value))]
     (when (and error (not silently))
       (throw
        (ex-info
         (str "Could not delete " value
              ": " (fmt.Sprint error))
         {:gobb/io :delete-file
          :path (str value)})))
     true)))

(defn resource [name]
  (host/resource-path name))
