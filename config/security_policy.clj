{:sources {:img "*"
           :style ["*.styles.example.com"
                   "https://inlinestyles.info"
                   :unsafe-inline]
           :media ["http://media.example.com"]
           :connect "https:"
           :frame :self
           :script ["https://trustedscripts.com"
                    :unsafe-eval]}
 :sandbox [:allow-forms
           :allow-scripts
           :allow-top-navigation]
 :report-uri "/some/report-uri"}
