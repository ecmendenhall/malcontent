(ns malcontent.core
  (:require [malcontent.policy  :refer [make-policy load-policy]]
            [malcontent.browser :refer [select-header]]
            [ring.adapter.jetty :refer [run-jetty]]))

(defn add-content-security-policy [handler]
  (fn [request]
    (let [response (handler request)
          headers  (:headers response)]
      (assoc response :headers
             (assoc headers
                    (select-header request)
                    (make-policy (load-policy)))))))
