(ns malcontent.middleware-spec
  (:require [speclj.core :refer :all]
            [malcontent.middleware :refer :all]
            [malcontent.policy-spec :refer [tricky-http-policy]]))

(def user-agent-string (str "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_4) "
                            "AppleWebKit/537.36 (KHTML, like Gecko) "
                            "Chrome/28.0.1500.95 Safari/537.36"))

(def mock-request  {:headers {"user-agent" user-agent-string}})

(describe "Wrapping responses"

          (it "should add a Content-Security-Policy header to outgoing responses"
              (should= {:headers {"Content-Security-Policy" tricky-http-policy
                                  "user-agent" user-agent-string}}
                       ((add-content-security-policy identity) mock-request)))
          (it "should load policy configurations from a custom location when passed as a keyword arg"
              (should= {:headers {"Content-Security-Policy" "default-src 'self'"
                                  "user-agent" user-agent-string}}
                       ((add-content-security-policy identity :config-path "config/alternate_policy.clj") mock-request))))
