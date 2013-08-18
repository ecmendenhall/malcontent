(ns malcontent.browser-spec
  (:require [speclj.core :refer :all]
            [malcontent.browser :refer :all]
            [malcontent.policy-spec :refer [tricky-http-policy]]))

(def empty-headers {:headers {}})

(def user-agent-string (str "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_4) "
                            "AppleWebKit/537.36 (KHTML, like Gecko) "
                            "Chrome/28.0.1500.95 Safari/537.36"))

(def mock-request  {:headers {"User-Agent" user-agent-string}})

(def chrome  {:version [28 0 1500 95] :name "Chrome" :family "CHROME"})
(def firefox {:version [23] :name "Firefox" :family "FIREFOX"})
(def opera   {:version [16] :name "Opera" :family "OPERA"})
(def safari  {:version [5] :name "Safari" :family "SAFARI"})

(def old-chrome  {:version [25 0 1500 95] :name "Chrome" :family "CHROME"})
(def old-firefox {:version [23] :name "Firefox" :family "FIREFOX"})
(def internet-explorer {:version [10] :name "Internet Explorer" :family "IE"})

(def standard-strings [
  "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.2 Safari/537.36"
  "Mozilla/5.0 (Windows NT 6.2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1467.0 Safari/537.36"
  "Mozilla/5.0 (X11; CrOS i686 3912.101.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.116 Safari/537.36"
  "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.93 Safari/537.36"
  "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.93 Safari/537.36"
  "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:23.0) Gecko/20131011 Firefox/23.0"
  "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20130406 Firefox/23.0"
  "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:24.0) Gecko/20100101 Firefox/24.0"])

(def webkit-strings [
  "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.60 Safari/537.17"
  "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1309.0 Safari/537.17"
  "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.15 (KHTML, like Gecko) Chrome/24.0.1295.0 Safari/537.15"
  "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.14 (KHTML, like Gecko) Chrome/24.0.1292.0 Safari/537.14"
  "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_0) AppleWebKit/537.4 (KHTML, like Gecko) Chrome/22.0.1229.79 Safari/537.4"
  "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/19.77.34.5 Safari/537.1"
  "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1062.0 Safari/536.3"
  "Mozilla/5.0 (Windows; U; Windows NT 6.1; sv-SE) AppleWebKit/533.19.4 (KHTML, like Gecko) Version/5.0.3 Safari/533.19.4"
  "Mozilla/5.0 (Windows; U; Windows NT 6.1; fr-FR) AppleWebKit/533.20.25 (KHTML, like Gecko) Version/5.0.4 Safari/533.20.27"
  "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_8; de-at) AppleWebKit/533.21.1 (KHTML, like Gecko) Version/5.0.5 Safari/533.21.1"
  "Mozilla/5.0 (iPad; CPU OS 5_1 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko ) Version/5.1 Mobile/9B176 Safari/7534.48.3"
  "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_3) AppleWebKit/534.55.3 (KHTML, like Gecko) Version/5.1.3 Safari/534.53.10"
  "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/537.13+ (KHTML, like Gecko) Version/5.1.7 Safari/534.57.2"
  "Mozilla/5.0 (iPad; CPU OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5355d Safari/8536.25"
  ])

(def gecko-strings [
  "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:22.0) Gecko/20130328 Firefox/22.0"
  "Mozilla/5.0 (Windows NT 6.1; rv:22.0) Gecko/20130405 Firefox/22.0"
  "Mozilla/5.0 (Windows NT 6.2; Win64; x64; rv:16.0.1) Gecko/20121011 Firefox/21.0.1"
  "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:16.0.1) Gecko/20121011 Firefox/21.0.1"
  "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:21.0) Gecko/20130331 Firefox/21.0"
  "Mozilla/5.0 (X11; Linux i686; rv:21.0) Gecko/20100101 Firefox/21.0"
  "Mozilla/5.0 (Windows NT 5.1; rv:21.0) Gecko/20100101 Firefox/21.0"
  "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:21.0) Gecko/20100101 Firefox/21.0"
  "Mozilla/5.0 (Windows NT 6.1; rv:6.0) Gecko/20100101 Firefox/19.0"
  "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:18.0)  Gecko/20100101 Firefox/18.0"
  "Mozilla/5.0 (Windows NT 6.2; Win64; x64; rv:16.0.1) Gecko/20121011 Firefox/16.0.1"
  "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:5.0) Gecko/20110619 Firefox/5.0"
  "Mozilla/5.0 (X11; Linux x86_64; rv:2.2a1pre) Gecko/20100101 Firefox/4.2a1pre"
  "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:2.0b8) Gecko/20100101 Firefox/4.0b8"
  "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:2.0b11pre) Gecko/20110128 Firefox/4.0b11pre"
  "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)"
  "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/5.0)"
  "Mozilla/5.0 (compatible; MSIE 10.0; Macintosh; Intel Mac OS X 10_7_3; Trident/6.0)"
  "Mozilla/5.0 (compatible; MSIE 10.6; Windows NT 6.1; Trident/5.0; InfoPath.2; SLCC1; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; .NET CLR 2.0.50727) 3gpp-gba UNTRUSTED/1.0"])

(defn correct-header? [predicate collection & options]
  (every? (fn [ua-string]
            (when options
              (when (options :verbose) (println "Testing user agent: " ua-string)))
            (predicate (parse-user-agent-string ua-string)))
          collection))

(describe "Examining requests"

          (it "should parse a user-agent string into a map"
              (should= chrome
                       (parse-user-agent-string user-agent-string)))

          (it "should extract the user-agent from the request headers"
              (should= chrome
                       (get-user-agent mock-request)))

          (it "should identify browsers that implement the standard header"
              (should (standard-header? chrome))
              (should (standard-header? firefox))
              (should (standard-header? opera)))

          (it "should identify browsers that implement the webkit header"
              (should (webkit-header? old-chrome))
              (should (webkit-header? safari)))

          (it "should identify browsers that implement the gecko header"
              (should (gecko-header? old-firefox))
              (should (gecko-header? internet-explorer)))

          (it "should select the correct header from the user-agent string"
              (should (correct-header? standard-header? standard-strings))
              (should (correct-header? webkit-header? webkit-strings))
              (should (correct-header? gecko-header? gecko-strings)))
          
          (it "should select the correct header from the request"
              (should= "Content-Security-Policy"
                       (select-header mock-request)))) 
