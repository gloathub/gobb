(ns babashka.curl
  (:refer-clojure :exclude [get])
  (:require [babashka.fs :as fs]
            [babashka.process :as process]))

(def ^:dynamic *defaults*
  {:compressed true
   :throw true})

(def unexceptional-statuses
  #{200 201 202 203 204 205 206 207
    300 301 302 303 304 307})

(defn coerce-key [value]
  (if (keyword? value)
    (subs (str value) 1)
    (str value)))

(defn url-encode [value]
  (net:url.QueryEscape (str value)))

(defn query-string [parameters]
  (strings.Join
   (mapv
    (fn [[key value]]
      (str (url-encode (coerce-key key))
           "="
           (url-encode value)))
    parameters)
   "&"))

(defn url-string [value]
  (if (map? value)
    (str (:scheme value)
         "://"
         (when-let [user (:user value)]
           (str user "@"))
         (:host value)
         (when-let [port (:port value)]
           (when (pos? port)
             (str ":" port)))
         (or (:path value) "")
         (when-let [query (:query value)]
           (str "?" query))
         (when-let [fragment (:fragment value)]
           (str "#" fragment)))
    (str value)))

(defn method-arguments [method]
  (when method
    (if (= :head method)
      ["--head"]
      ["--request"
       (strings.ToUpper (name method))])))

(defn header-arguments [headers]
  (vec
   (mapcat
    (fn [[key value]]
      ["-H" (str (coerce-key key) ": " value)])
    (or headers []))))

(defn body-arguments [options]
  (let [body (:body options)]
    (cond
      (nil? body) nil
      (string? body) ["--data-raw" body]
      (or (instance? File body)
          (instance? Path body))
      ["--data-binary" (str "@" body)]
      :else
      (throw
       (ex-info
        "babashka.curl stream request bodies are not supported yet"
        {:gobb/curl :stream-request-body})))))

(defn form-arguments [parameters]
  (vec
   (mapcat
    (fn [[key value]]
      ["--data"
       (str (url-encode (coerce-key key))
            "="
            (url-encode value))])
    (or parameters []))))

(defn request-command [options header-file]
  (let [base
        (cond-> ["curl"
                 "--show-error"
                 "--dump-header"
                 (str header-file)]
          (not (false? (:compressed options)))
          (conj "--compressed")
          (if-let [[_ silent] (find options :silent)]
            silent
            true)
          (conj "--silent")
          (:follow-redirects options true)
          (conj "--location"))
        url (url-string (:url options))
        query (when-let [parameters (:query-params options)]
                (query-string parameters))
        url (str url
                 (when (seq query)
                   (str (if (strings.Contains url "?")
                          "&"
                          "?")
                        query)))]
    (vec
     (concat
      base
      (method-arguments (:method options))
      (header-arguments (:headers options))
      (when-let [accept (:accept options)]
        ["-H"
         (str "Accept: "
              (if (= :json accept)
                "application/json"
                accept))])
      (body-arguments options)
      (form-arguments (:form-params options))
      (when-let [auth (:basic-auth options)]
        ["--user"
         (if (sequential? auth)
           (strings.Join (mapv str auth) ":")
           (str auth))])
      (:raw-args options)
      [url]))))

(defn parse-headers [lines]
  (reduce
   (fn [[status headers] line]
     (if (strings.HasPrefix line "HTTP/")
       (let [pieces (strings.Fields line)]
         [(when (< 1 (count pieces))
            (let [[number error]
                  (strconv.Atoi (nth pieces 1))]
              (when (nil? error) number)))
          headers])
       (let [separator (strings.Index line ":")]
         (if (neg? separator)
           [status headers]
           (let [key (strings.ToLower
                      (subs line 0 separator))
                 value (strings.TrimSpace
                        (subs line (inc separator)))]
             [status
              (update
               headers key
               (fn [previous]
                 (cond
                   (nil? previous) value
                   (vector? previous)
                   (conj previous value)
                   :else [previous value])))])))))
   [nil {}]
   lines))

(defn should-throw? [response options]
  (and (:throw options)
       (or (not (zero? (:exit response)))
           (let [status (:status response)]
             (and status
                  (not
                   (contains? unexceptional-statuses
                              status)))))))

(defn response-message [response]
  (cond
    (:status response)
    (str "babashka.curl: status " (:status response))

    (not (strings.EqualFold
          "" (strings.TrimSpace (or (:err response) ""))))
    (:err response)

    :else "babashka.curl: error"))

(defn request [request-options]
  (let [options (merge *defaults* request-options)
        header-file
        (fs/create-temp-file
         {:prefix "gobb-curl-"
          :suffix ".headers"})
        command (request-command options header-file)]
    (try
      (let [process
            (process/process
             {:out (if (= :bytes (:as options))
                     :bytes
                     :string)
              :err :string}
             command)
            result @process
            [status headers]
            (parse-headers
             (if (fs/exists? header-file)
               (fs/read-all-lines header-file)
               []))
            body
            (if (= :stream (:as options))
              (strings.NewReader (or (:out result) ""))
              (:out result))
            response
            {:status status
             :headers headers
             :body body
             :err (:err result)
             :process process
             :exit (:exit result)}
            response
            (if (:debug options)
              (assoc response
                     :command command
                     :options options)
              response)]
        (if (should-throw? response options)
          (throw
           (ex-info (response-message response)
                    response))
          response))
      (finally
        (fs/delete-if-exists header-file)))))

(defn delete
  ([url] (delete url nil))
  ([url options]
   (request (assoc (or options {})
                   :url url
                   :method :delete))))

(defn head
  ([url] (head url nil))
  ([url options]
   (request (assoc (or options {})
                   :url url
                   :method :head))))

(defn get
  ([url] (get url nil))
  ([url options]
   (request (assoc (or options {}) :url url))))

(defn post
  ([url] (post url nil))
  ([url options]
   (request (assoc (or options {})
                   :url url
                   :method :post))))

(defn put
  ([url] (put url nil))
  ([url options]
   (request (assoc (or options {})
                   :url url
                   :method :put))))

(defn patch
  ([url] (patch url nil))
  ([url options]
   (request (assoc (or options {})
                   :url url
                   :method :patch))))
