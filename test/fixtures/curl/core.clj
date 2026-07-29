(require '[babashka.curl :as curl]
         '[clojure.string :as string])

(defn normalize-command [command]
  (let [index
        (loop [index 0]
          (cond
            (not (< index (count command))) nil
            (string/starts-with?
             (nth command index)
             "--dump-header") index
            :else (recur (inc index))))]
    (if (number? index)
      (assoc command (inc index) "<headers>")
      command)))

(let [source (first *command-line-args*)
      base-url (second *command-line-args*)
      url (str "file://" source)
      plain (curl/get url)
      bytes (curl/get url {:as :bytes})
      debug
      (curl/get
       url
       {:headers {:accept "text/plain"}
        :accept "text/plain"
        :query-params [[:one "two words"]
                       [:namespaced/key 42]]
        :raw-args ["--max-time" "2"]
        :throw false
        :debug true})
      failure
      (curl/get "gobb-unknown://example"
                {:throw false})
      request
      (curl/get
       (str base-url "/request")
       {:headers {"X-Request-Test" "header"}
        :query-params {:message "two words"}})
      post
      (curl/post (str base-url "/echo")
                 {:body "posted"})
      form
      (curl/post
       (str base-url "/echo")
       {:form-params [[:name "two words"]
                      [:count 2]]})
      redirect
      (curl/get (str base-url "/redirect"))
      missing
      (curl/get (str base-url "/missing")
                {:throw false})]
  (prn
   {:plain (select-keys plain
                        [:status :headers :body :err :exit])
    :bytes (vec (:body bytes))
    :command (normalize-command (:command debug))
    :http
    {:request [(:status request)
               (:body request)
               (get-in request
                       [:headers "x-gobb-test"])]
     :post [(:status post) (:body post)]
     :form [(:status form) (:body form)]
     :redirect [(:status redirect)
                (:body redirect)]
     :missing [(:status missing)
               (:body missing)
               (:exit missing)]}
    :failure [(:status failure)
              (:exit failure)
              (if (re-find
                   #"Protocol.*not supported|Unsupported protocol"
                   (:err failure))
                true
                false)]}))
