(ns hiccup.render
  (:require [clojure.string :as str]))

(defn raw-string [values]
  {:hiccup/raw (apply str values)})

(defn escape-html [value]
  (-> (str value)
      (str/replace "&" "&amp;")
      (str/replace "<" "&lt;")
      (str/replace ">" "&gt;")
      (str/replace "\"" "&quot;")
      (str/replace "'" "&#x27;")))

(defn parse-tag [tag]
  (let [[_ name id classes]
        (re-matches #"([^#.]+)(?:#([^#.]+))?((?:\.[^#.]+)*)"
                    (name tag))]
    {:name name
     :id id
     :class (when (seq classes)
              (str/join " " (rest (str/split classes #"\."))))}))

(defn style-value [style]
  (if (map? style)
    (apply str
           (map (fn [[key value]]
                  (str (name key) ":" value ";"))
                style))
    (str style)))

(defn class-value [value]
  (if (sequential? value)
    (str/join " " (remove nil? value))
    (str value)))

(defn attribute-value [key value]
  (case key
    :style (style-value value)
    :class (class-value value)
    (str value)))

(defn render-attributes [attributes escape?]
  (apply
   str
   (keep
    (fn [[key value]]
      (cond
        (or (nil? value) (false? value)) nil
        (true? value) (str " " (name key))
        :else
        (let [value (attribute-value key value)
              value (if escape? (escape-html value) value)]
          (str " " (name key) "=\"" value "\""))))
    attributes)))

(def void-tags
  #{"area" "base" "br" "col" "embed" "hr" "img" "input"
    "link" "meta" "param" "source" "track" "wbr"})

(declare render-value)

(defn render-element [element escape?]
  (let [[tag & body] element
        {:keys [name id class]} (parse-tag tag)
        [attributes children]
        (if (map? (first body))
          [(first body) (next body)]
          [{} body])
        attributes
        (cond-> attributes
          class
          (update :class
                  (fn [existing]
                    (str/join " "
                              (remove nil? [class existing]))))
          (and id (not (contains? attributes :id)))
          (assoc :id id))
        opening (str "<" name
                     (render-attributes attributes escape?)
                     ">")]
    (if (contains? void-tags name)
      opening
      (str opening
           (apply str (map #(render-value % escape?) children))
           "</" name ">"))))

(defn render-value [value escape?]
  (cond
    (nil? value) ""
    (and (map? value)
         (contains? value :hiccup/raw))
    (:hiccup/raw value)
    (and (vector? value)
         (or (keyword? (first value))
             (symbol? (first value))
             (string? (first value))))
    (render-element value escape?)
    (sequential? value)
    (apply str (map #(render-value % escape?) value))
    :else
    (if escape?
      (escape-html value)
      (str value))))

(defn render-root [values escape?]
  (apply str (map #(render-value % escape?) values)))
