(ns malcontent.core-spec
  (:require [speclj.core :refer :all]
            [malcontent.core :refer :all]
            [malcontent.policy-spec :refer [tricky-http-policy]]))

(def user-agent-string (str "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_4) "
                            "AppleWebKit/537.36 (KHTML, like Gecko) "
                            "Chrome/28.0.1500.95 Safari/537.36"))

(def mock-request  {:headers {"user-agent" user-agent-string}})

(describe "Wrapping responses"

          (it "should add a Content-Security-Policy header to outgoing responses"
              (should= {:headers {"Content-Security-Policy" tricky-http-policy
                                  "user-agent" user-agent-string}}
                       ((add-content-security-policy identity) mock-request))))
