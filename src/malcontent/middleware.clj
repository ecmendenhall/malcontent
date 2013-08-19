(ns malcontent.middleware
  (:require [malcontent.policy  :refer [make-policy load-policy]]
            [malcontent.browser :refer [select-header]]
            [ring.adapter.jetty :refer [run-jetty]]))

(defn add-content-security-policy
  ([handler & {:keys [config-path]}]
     (fn [request]
      (let [response (handler request)
            headers  (:headers response)]
        (assoc response :headers
               (assoc headers
                 (select-header request)
                 (make-policy (if config-path
                                (load-policy config-path)
                                (load-policy)))))))))
                              

