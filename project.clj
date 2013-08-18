(defproject malcontent "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.5.0"]
                 [ring "1.2.0"]
                 [net.sf.uadetector/uadetector-resources "2013.02"]
                 [org.slf4j/slf4j-api "1.7.5"]
                 [org.slf4j/slf4j-log4j12 "1.7.5"]]
  :profiles {:dev {:dependencies [[speclj "2.5.0"]]}}
  :plugins [[speclj "2.5.0"]]
  :test-paths ["spec"])
