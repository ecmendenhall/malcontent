(ns malcontent.policy
  (:require [clojure.edn :as edn]))

(defprotocol source-value
  (to-source [this]))

(extend-protocol source-value
  clojure.lang.Keyword
  (to-source [this]
    (str "'" (name this) "'")))

(extend-protocol source-value
  java.lang.String
  (to-source [this]
    this))

(defn map-join [func separator collection]
  (apply str (interpose separator (map func collection))))

(defn to-values [collection]
  (map-join to-source " " collection))

(defprotocol sources
  (source-values [this]))

(extend-protocol sources
  java.lang.String
  (source-values [this]
    this))

(extend-protocol sources
  clojure.lang.PersistentVector
  (source-values [this]
    (to-values this)))

(extend-protocol sources
  clojure.lang.Keyword
  (source-values [this]
    (to-source this)))

(defn make-source-directive [[source-type sources]]
  (str (name source-type)
       "-src "
       (source-values sources)))

(defn get-sources [sources-map]
  (map-join make-source-directive "; " (seq sources-map)))

(defn make-directive [[directive value]]
  (str (name directive)
       " "
       (source-values value)))

(defn get-directives [directives-map]
  (map-join make-directive "; " (seq directives-map)))

(defn make-policy [policy-map]
  (let [sources    (policy-map :sources)
        directives (dissoc policy-map :sources)]
    (if (nil? sources)
      (get-directives directives)    
      (str (get-sources sources)
           (when-not (empty? directives)
             (str "; " (get-directives directives)))))))

(defn load-policy
  ([]
     (edn/read-string (slurp "config/security_policy.clj")))
  ([filepath]
     (edn/read-string (slurp filepath))))
