(ns babashka.http-client
  (:refer-clojure :exclude [get])
  (:require [babashka.curl :as curl]
            [gobb.capabilities :as capabilities]))

(def default-client-opts
  {:follow-redirects :normal
   :request
   {:headers
    {:accept "*/*"
     :accept-encoding ["gzip" "deflate"]
     :user-agent "babashka.http-client/0.4.23"}}})

(defn unsupported-constructor [constructor options]
  (capabilities/unsupported!
   :network
   {:operation constructor
    :options options}))

(defn ->ProxySelector [options]
  (unsupported-constructor :proxy-selector options))

(defn ->SSLContext [options]
  (unsupported-constructor :ssl-context options))

(defn ->Authenticator [options]
  (unsupported-constructor :authenticator options))

(defn ->CookieHandler [options]
  (unsupported-constructor :cookie-handler options))

(defn ->SSLParameters [options]
  (unsupported-constructor :ssl-parameters options))

(defn ->Executor [options]
  (unsupported-constructor :executor options))

(defn client [options]
  {:client options
   :request (:request options)
   :type :babashka.http-client/client})

(def default-client
  (client default-client-opts))

(defn merge-request [defaults request]
  (assoc
   (merge defaults request)
   :headers
   (merge (:headers defaults)
          (:headers request))))

(defn query-entries [parameters]
  (mapcat
   (fn [[key value]]
     (if (and (coll? value)
              (seqable? value))
       (mapv (fn [item] [key item]) value)
       [[key value]]))
   parameters))

(defn client-settings [client]
  (if (and (map? client)
           (= :babashka.http-client/client
              (:type client)))
    (:client client)
    default-client-opts))

(defn custom-client [client]
  (cond
    (fn? client) client
    (and (map? client)
         (fn? (:client client)))
    (:client client)
    :else nil))

(defn response-map [response request]
  (-> response
      (dissoc :err :exit :process)
      (assoc :request request
             :uri (:uri request)
             :version :http1.1)))

(defn native-request [request]
  (let [client (:client request)
        settings (client-settings client)
        defaults (:request settings)
        request (merge-request defaults request)]
    (if-let [handler (custom-client client)]
      (handler request)
      (let [basic-auth
            (when-let [auth (:basic-auth request)]
              (if (map? auth)
                [(:user auth) (:pass auth)]
                auth))
            headers
            (cond-> (:headers request)
              (:oauth-token request)
              (assoc :authorization
                     (str "Bearer " (:oauth-token request))))
            follow-redirects
            (not= :never (:follow-redirects settings))
            response
            (curl/request
             (cond-> {:url (or (:uri request)
                               (:url request))
                      :method (:method request)
                      :headers headers
                      :query-params
                      (when-let [parameters
                                 (:query-params request)]
                        (query-entries parameters))
                      :form-params (:form-params request)
                      :body (:body request)
                      :basic-auth basic-auth
                      :as (:as request)
                      :throw (:throw request true)
                      :follow-redirects follow-redirects}
               (:timeout request)
               (assoc :raw-args
                      ["--max-time"
                       (str (/ (:timeout request)
                               1000.0))])))]
        (response-map response request)))))

(defn request [request]
  (if (:async request)
    (future
      (let [response (native-request request)]
        (if-let [then-fn (:async-then request)]
          (then-fn response)
          response)))
    (native-request request)))

(defn get
  ([uri] (get uri nil))
  ([uri options]
   (request (assoc (or options {})
                   :uri uri
                   :method :get))))

(defn delete
  ([uri] (delete uri nil))
  ([uri options]
   (request (assoc (or options {})
                   :uri uri
                   :method :delete))))

(defn head
  ([uri] (head uri nil))
  ([uri options]
   (request (assoc (or options {})
                   :uri uri
                   :method :head))))

(defn post
  ([uri] (post uri nil))
  ([uri options]
   (request (assoc (or options {})
                   :uri uri
                   :method :post))))

(defn patch
  ([uri] (patch uri nil))
  ([uri options]
   (request (assoc (or options {})
                   :uri uri
                   :method :patch))))

(defn put
  ([uri] (put uri nil))
  ([uri options]
   (request (assoc (or options {})
                   :uri uri
                   :method :put))))
