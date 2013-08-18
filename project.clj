(defproject malcontent "0.1.0-SNAPSHOT"
  :description "Ring middleware for HTTP Content Security Policy"
  :url "http://github.com/ecmendenhall/malcontent"
  :license {:name "Apache License, Version 2.0"
            :url "https://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [[org.clojure/clojure "1.5.0"]
                 [ring "1.2.0"]
                 [net.sf.uadetector/uadetector-resources "2013.02"]]
  :profiles {:dev {:dependencies [[speclj "2.5.0"]]}}
  :plugins [[speclj "2.5.0"]]
  :test-paths ["spec"])
