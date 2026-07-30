(ns babashka.nrepl.server
  (:require [gobb.servers :as servers]))

(defn start-server!
  ([] (start-server! nil))
  ([options]
   (let [address
         (cond
           (nil? options) nil
           (string? options) options
           (map? options)
           (str (or (:host options)
                    (:bind options)
                    "127.0.0.1")
                ":"
                (or (:port options) 0))
           :else (str options))]
     (servers/start-server! :nrepl address))))

(defn stop-server! [server]
  (servers/stop-server! server))
