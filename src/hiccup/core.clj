(ns hiccup.core
  (:require [hiccup.render :as render]
            [hiccup2.core :as hiccup2]))

(defn h [value]
  (render/escape-html value))

(defmacro html [& values]
  `(hiccup.render/render-root [~@values] false))
