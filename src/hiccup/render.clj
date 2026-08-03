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

(defn render-attributes [attributes escape? mode]
  (apply
   str
   (keep
    (fn [[key value]]
      (cond
        (or (nil? value) (false? value)) nil
        (true? value) (if (#{:html :sgml} mode)
                        (str " " (name key))
                        (str " " (name key) "=\"" (name key) "\""))
        :else
        ;; Hiccup always escapes attribute values. Its legacy
        ;; :escape-strings? option applies only to element content.
        (let [value (escape-html (attribute-value key value))]
          (str " " (name key) "=\"" value "\""))))
    (sort-by (comp name key) attributes))))

(def void-tags
  #{"area" "base" "br" "col" "embed" "hr" "img" "input"
    "link" "meta" "param" "source" "track" "wbr"})

(declare render-value)

(defn render-element [element escape? mode]
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
        attributes-text (render-attributes attributes escape? mode)
        empty? (empty? children)]
    (cond
      (contains? void-tags name)
      (str "<" name attributes-text
           (if (#{:xhtml :xml} mode) " />" ">"))

      (and empty? (= :xml mode))
      (str "<" name attributes-text " />")

      (and empty? (= :sgml mode))
      (str "<" name attributes-text ">")

      :else
      (str "<" name attributes-text ">"
           (apply str (map #(render-value % escape? mode) children))
           "</" name ">"))))

(defn render-value [value escape? mode]
  (cond
    (nil? value) ""
    (and (map? value)
         (contains? value :hiccup/raw))
    (:hiccup/raw value)
    (and (vector? value)
         (or (keyword? (first value))
             (symbol? (first value))
             (string? (first value))))
    (render-element value escape? mode)
    (vector? value)
    (throw
     (github.com:glojurelang:glojure:pkg:lang.NewIllegalArgumentError
      "Hiccup element vectors require a tag name"))
    (sequential? value)
    (apply str (map #(render-value % escape? mode) value))
    (keyword? value)
    (name value)
    :else
    (if escape?
      (escape-html value)
      (str value))))

(defn render-root
  ([values escape?]
   (render-root values escape? :html))
  ([values escape? default-mode]
   (let [options (when (and (map? (first values))
                            (contains? (first values) :mode))
                   (first values))
         values (if options (next values) values)
         mode (or (:mode options) default-mode)]
     (apply str (map #(render-value % escape? mode) values)))))
