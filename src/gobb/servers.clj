(ns gobb.servers
  (:require [gobb.capabilities :as capabilities]))

(def running-servers (atom #{}))

(defn parse-address [address default-port]
  (let [address (or address (str default-port))
        separator (strings.LastIndex address ":")
        [host port-text]
        (if (neg? separator)
          ["127.0.0.1" address]
          [(let [value (subs address 0 separator)]
             (if (empty? value) "127.0.0.1" value))
           (subs address (inc separator))])
        [port error] (strconv.Atoi port-text)]
    (when (or error (neg? port) (< 65535 port))
      (throw
       (ex-info
        (str "invalid server address: " address)
        {:gobb/address address})))
    {:host host :port port}))

(defn create-server! [kind address]
  (capabilities/require! :network {:operation kind})
  (let [{:keys [host port]} (parse-address
                             address
                             (if (= kind :nrepl) 1667 1666))
        [server error]
        (case kind
          :nrepl
          (github.com:glojurelang:glojure:pkg:nrepl.Start
           host port "")

          :socket-repl
          (github.com:glojurelang:glojure:pkg:srepl.Start
           host port "")

          (throw
           (ex-info "unknown server kind"
                    {:gobb/server-kind kind})))]
    (when error
      (throw error))
    (swap! running-servers conj server)
    server))

(defn start-server! [kind address]
  (let [server (create-server! kind address)]
    (future (.Serve server))
    server))

(defn stop-server! [server]
  (when (contains? @running-servers server)
    (.Stop server)
    (swap! running-servers disj server)
    true))

(defn stop-servers! []
  (doseq [server @running-servers]
    (stop-server! server))
  nil)

(defn print-started! [kind server]
  (let [host (.Host server)
        port (.Port server)]
    (binding [*out* *err*]
      (case kind
        :nrepl
        (do
          (println
           (format "Started nREPL server at %s:%d" host port))
          (println
           "For more info visit: https://book.babashka.org/#_nrepl"))

        :socket-repl
        (println
         (format "Babashka socket REPL started at %s:%d"
                 host port))))
    {:host host :port port}))

(defn serve! [kind address]
  (let [server (create-server! kind address)]
    (print-started! kind server)
    (.Serve server)))
