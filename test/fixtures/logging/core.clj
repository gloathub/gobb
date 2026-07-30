(require '[clojure.tools.logging :as log]
         '[taoensso.timbre :as timbre])

(def tools-return (log/info "tools" 42))

(def timbre-return (timbre/info "timbre" 43))

(println
 (pr-str
  {:tools-return tools-return
   :timbre-return timbre-return}))
