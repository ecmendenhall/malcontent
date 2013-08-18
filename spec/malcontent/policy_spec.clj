(ns malcontent.policy-spec
  (:require [speclj.core :refer :all]
            [malcontent.policy :refer :all]))

(def clj-policy {:sources {:script ["https://apis.google.com"
                                    "https://platform.twitter.com"]
                           :frame "https://plusone.google.com"}
                 :sandbox :allow-forms
                 :report-uri "/some-report-uri"})

(def source-policy (str "frame-src https://plusone.google.com; "
                        "script-src https://apis.google.com "
                        "https://platform.twitter.com"))

(def simple-http-policy (str source-policy "; "
                             "sandbox 'allow-forms'; "
                             "report-uri /some-report-uri"))

(def tricky-clj-policy {:sources {:style ["*.styles.example.com"
                                          "https://inlinestyles.info"
                                          :unsafe-inline]
                                  :connect "https:"
                                  :script ["https://trustedscripts.com"
                                           :unsafe-eval]
                                  :img "*"
                                  :media ["http://media.example.com"]
                                  :frame :self}
                        :sandbox [:allow-forms
                                  :allow-scripts
                                  :allow-top-navigation]
                        :report-uri "/some/report-uri"})

(def tricky-http-policy (str "img-src *; "
                             "style-src *.styles.example.com "
                             "https://inlinestyles.info "
                             "'unsafe-inline'; "
                             "media-src http://media.example.com; "
                             "connect-src https:; "
                             "frame-src 'self'; "
                             "script-src https://trustedscripts.com " 
                             "'unsafe-eval'; "
                             "sandbox 'allow-forms' 'allow-scripts' "
                             "'allow-top-navigation'; "
                             "report-uri /some/report-uri"))

(describe "Constructing policies"

          (it "converts keywords to source values"
              (should= "'none'"
                       (to-source :none)))

          (it "does not convert strings"
              (should= "https://apis.google.com"
                       (to-source "https://apis.google.com")))

          (it "maps over a collection, then joins the results"
              (should= "2~*3~*4~*5"
                       (map-join inc "~*" [1 2 3 4])))
          
          (it "converts a list of sources into a string"
              (should= "https://apis.google.com https://platform.twitter.com"
                       (source-values ((clj-policy :sources) :script))))

          (it "converts a single source into a string"
              (should= "https://apis.google.com"
                       (source-values "https://apis.google.com")))
          
          (it "converts a keyword/source pair into a directive"
              (should= "frame-src https://plusone.google.com"
                       (make-source-directive [:frame "https://plusone.google.com"])))

          (it "converts a simple keyword/value pair into a directive"
              (should= "sandbox 'allow-forms'"
                       (make-directive [:sandbox :allow-forms])))
          
          (it "converts the sources map into a policy string"
              (should= source-policy
                       (get-sources (clj-policy :sources))))

          (it "converts the remaining policy into a policy string"
              (should= "sandbox 'allow-forms'; report-uri /some-report-uri"
                       (get-directives (dissoc clj-policy :sources))))

          (it "converts a full policy map into a policy string"
              (should= simple-http-policy
                       (make-policy clj-policy)))

          (it "converts a policy map without sources into a valid policy string"
              (should= "sandbox 'allow-forms'; report-uri /some-report-uri"
                       (make-policy (dissoc clj-policy :sources))))

          (it "converts a policy map without directives into a valid policy string"
              (should= (get-sources (clj-policy :sources))
                       (make-policy (dissoc clj-policy :sandbox :report-uri))))

          (it "converts policy maps with special directives into valid policy strings"
              (should= tricky-http-policy
                       (make-policy tricky-clj-policy)))

          (it "loads policy maps from the default location"
              (should= tricky-http-policy
                       (make-policy (load-policy)))))
