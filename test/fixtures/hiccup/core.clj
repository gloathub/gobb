(require '[hiccup.core :as hiccup]
         '[hiccup2.core :as hiccup2])

(println
 (pr-str
  {:escaped
   (str
    (hiccup2/html
     [(keyword "div#app.card")
      [:span {:title "a&b"} "<hello>"]
      (hiccup2/raw "<strong>raw</strong>")]))
   :legacy
   (hiccup/html [:p {:class ["one" "two"]} "<legacy>"])
   :styles
   (str
    (hiccup2/html
     [:div {:style {:color "red"
                    :display "block"}}
      "styled"]))}))
