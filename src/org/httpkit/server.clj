(ns org.httpkit.server
  (:require [gobb.capabilities :as capabilities]))

(defn run-server
  "Start a Ring HTTP server. Returns a zero-argument stop function carrying
  `:local-port` and `:server` metadata, matching http-kit's common API."
  [handler options]
  (capabilities/require! :network {:operation :http-server})
  (let [host (or (:ip options) (:host options) "0.0.0.0")
        port (or (:port options) 8090)
        [server error]
        (github.com:glojurelang:glojure:pkg:httpserver.Start
         handler host port)]
    (when error
      (throw error))
    (with-meta
      (fn
        ([] (.Stop server))
        ([_timeout] (.Stop server)))
      {:local-port (.Port server)
       :server server})))

(defn server-port [server]
  (.Port server))

(defn server-stop!
  ([server] (.Stop server))
  ([server _timeout] (.Stop server)))

(defn server-status [_server]
  :running)

(defn unsupported-websocket! [operation]
  (throw
   (ex-info
    (str "org.httpkit.server/" operation
         " is not implemented by Gobb yet")
    {:type :gobb/unsupported-httpkit-operation
     :operation operation})))

(defn as-channel [& _]
  (unsupported-websocket! "as-channel"))

(defn with-channel [& _]
  (unsupported-websocket! "with-channel"))

(defn send! [& _]
  (unsupported-websocket! "send!"))

(defn close [& _]
  (unsupported-websocket! "close"))

(defn on-close [& _]
  (unsupported-websocket! "on-close"))

(defn websocket-handshake-check [& _]
  (unsupported-websocket! "websocket-handshake-check"))

(defn send-websocket-handshake! [& _]
  (unsupported-websocket! "send-websocket-handshake!"))

(defn send-checked-websocket-handshake! [& _]
  (unsupported-websocket! "send-checked-websocket-handshake!"))

(defn sec-websocket-accept [& _]
  (unsupported-websocket! "sec-websocket-accept"))
