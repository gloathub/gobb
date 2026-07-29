(require '[babashka.http-client :as http])

(let [base-url (first *command-line-args*)
      request
      (http/get
       (str base-url "/request")
       {:headers {"X-Request-Test" "header"}
        :query-params {:message "two words"}})
      post
      (http/post (str base-url "/echo")
                 {:body "posted"})
      form
      (http/post
       (str base-url "/echo")
       {:form-params {:name "two words"
                      :count 2}})
      redirect
      (http/get (str base-url "/redirect"))
      no-redirect
      (http/get
       (str base-url "/redirect")
       {:client
        (http/client
         {:follow-redirects :never})})
      missing
      (http/get (str base-url "/missing")
                {:throw false})
      async
      @(http/get (str base-url "/echo")
                 {:async true})
      custom
      (http/get
       "https://unused.invalid"
        {:client
        (fn [request]
          {:status 200
           :body (name (:method request))
           :headers {}})})]
  (prn
   {:request [(:status request)
              (:body request)
              (get-in request
                      [:headers "x-gobb-test"])]
    :post [(:status post) (:body post)]
    :form [(:status form) (:body form)]
    :redirect [(:status redirect)
               (:body redirect)]
    :no-redirect [(:status no-redirect)
                  (get-in no-redirect
                          [:headers "location"])]
    :missing [(:status missing)
              (:body missing)]
    :async [(:status async) (:body async)]
    :custom [(:status custom) (:body custom)]}))
