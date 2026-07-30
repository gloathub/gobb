(require '[babashka.cli :as cli])

(println
 (pr-str
  {:basic
   (cli/parse-opts
    ["--port" "8080" "--verbose" "input.txt"]
    {:coerce {:port :long
              :verbose :boolean}})
   :aliases
   (cli/parse-opts
    ["-p" "9000" "-v"]
    {:alias {:p :port
             :v :verbose}
     :coerce {:port :long
              :verbose :boolean}})
   :collect
   (cli/parse-opts
    ["--tag" "one" "--tag" "two"]
    {:collect {:tag []}})
   :cmds
   (cli/parse-cmds ["build" "app" "--force"])
   :coerce
   [(cli/coerce "42" :long)
    (cli/coerce ":value" :keyword)
    (cli/auto-coerce "true")
    (cli/auto-coerce "plain")]}))
