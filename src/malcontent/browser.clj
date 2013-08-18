(ns malcontent.browser
  (:import [net.sf.uadetector UserAgent UserAgentStringParser]
           [net.sf.uadetector.service UADetectorServiceFactory]))

(defn parse-user-agent-string [user-agent-string]
  (let [parser (UADetectorServiceFactory/getResourceModuleParser)
        user-agent (.parse parser user-agent-string)]
    (-> {}
        (assoc :family (str (.getFamily user-agent)))
        (assoc :name (.getName user-agent))
        (assoc :version (vec (map (fn [number-str]
                                    (try (Integer/parseInt number-str)
                                         (catch NumberFormatException e 0)))
                                  (.getGroups (.getVersionNumber user-agent))))))))

(defn get-user-agent [request]
  (let [headers    (request :headers)
        user-agent (headers "user-agent")]
    (parse-user-agent-string user-agent)))

(defn new-enough? [family version]
  (cond (= "CHROME"  family) (>= version 25)
        (= "FIREFOX" family) (>= version 23)
        (= "OPERA"   family) (>= version 15)
        (= "OPERA_MOBILE" family) (>= version 14)
        :else false))

(defn standard-header? [browser]
  (let [browser-family    (browser :family)
        browser-version (first (browser :version))]
    (and (contains? #{"CHROME" "FIREFOX" "OPERA" "OPERA_MOBILE"} browser-family)
         (new-enough? browser-family browser-version))))

(defn gecko-header? [browser]
  (let [browser-family    (browser :family)
        browser-version (first (browser :version))]
    (or (and (= "FIREFOX" browser-family)
             (<= browser-version 23))
        (and (= "IE" browser-family)
             (<= browser-version 10)))))

(defn webkit-header? [browser]
  (let [browser-family  (browser :family)
        browser-version (first (browser :version))]
    (or (and (= "CHROME" browser-family)
             (<= browser-version 25))
        (and (= "CHROME_MOBILE" browser-family)
             (<= browser-version 28))
        (and (= "BLACKBERRY_BROWSER" browser-family)
             (<= 10 browser-version))
        (and (contains? #{"SAFARI" "MOBILE_SAFARI"} browser-family)
             (>= browser-version 5)))))

(defn select-header [request]
  (let [browser (get-user-agent request)]
    (cond (standard-header? browser)   "Content-Security-Policy"
          (gecko-header?    browser)   "X-Content-Security-Policy"
          (webkit-header?   browser)   "X-Webkit-CSP"
          :else "blah")))
