(ns hiccup2.core
  (:require [hiccup.render :as render]))

(defn raw [& values]
  (render/raw-string values))

(defmacro html [& values]
  `(hiccup.render/render-root [~@values] true))
