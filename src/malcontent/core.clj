(ns malcontent.core
  (:require [malcontent.policy  :refer [make-policy load-policy]]
            [malcontent.browser :refer [select-header]]
            [ring.adapter.jetty :refer [run-jetty]])
  (:import [org.apache.log4j BasicConfigurator]))

(defn handler [request]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body (str "Request:\n\n"
              (with-out-str (println request)))})

(defn add-content-security-policy [handler]
  (fn [request]
    (let [response (handler request)
          headers  (:headers response)]
      (assoc response :headers
             (assoc headers
                    (select-header request)
                    (make-policy (load-policy)))))))

(defn -main [& args]
  (BasicConfigurator/configure)
  (run-jetty (add-content-security-policy handler) {:port 5000}))
