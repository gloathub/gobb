(ns clojure.core.server
  (:require [gobb.servers :as host-servers]))

(defonce servers (atom {}))

(defn start-server [options]
  (let [name (or (:name options) "gobb")
        address (str (or (:address options) "127.0.0.1")
                     ":"
                     (:port options))
        server (host-servers/start-server!
                :socket-repl address)]
    (swap! servers assoc name server)
    server))

(defn stop-server
  ([] (stop-server "gobb"))
  ([name]
   (when-let [server (get @servers name)]
     (swap! servers dissoc name)
     (host-servers/stop-server! server))))

(defn stop-servers []
  (doseq [name (keys @servers)]
    (stop-server name))
  nil)
