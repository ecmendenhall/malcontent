# Malcontent
Content Security Policy is a new browser security mechanism to prevent [cross site scripting](https://www.owasp.org/index.php/Top_10_2013-A3-Cross-Site_Scripting_(XSS\)) attacks. By sending a 'Content-Security-Policy' header in HTTP responses, web applications can provide rules and restrictions for client side scripts, plugins, frames, and other resources to be enforced by the browser. Whitelisting trusted resources effectively shuts down most XSS attacks, and it already works in [most modern browsers](http://caniuse.com/contentsecuritypolicy).

Malcontent is a simple middleware handler for adding the CSP header to Ring applications. Simply specify a security policy as a Clojure map and malcontent will include it in responses to supported browsers.

## Usage
Malcontent looks for a policy file at `config/security_policy.clj`. Here are the examples from the [HTML5 Rocks introduction to CSP](http://www.html5rocks.com/en/tutorials/security/content-security-policy/#real-world-usage) as malcontent maps:

__Social media widgets:__

    {:sources {:script ["https://apis.google.com"
                        "https://platform.twitter.com"]
               :frame  ["https://plusone.google.com"
                        "https://facebook.com"
                        "https://platform.twitter.com"]}}

__Lockdown:__

    {:sources {:default :none
               :script  "https://cdn.mybank.net"
               :style   "https://cdn.mybank.net"
               :img     "https://cdn.mybank.net"
               :connect "https://api.mybank.com"
               :frame   :self}}

__SSL Only__:
    {:sources {:default "https:"
               :script  ["https:" :unsafe-inline]
               :style   ["https:" :unsafe-inline]}}

In addition to sources, policy maps may include the `:sandbox` and
`:report-uri` directives. Sources may be specified as strings,
keywords, or vectors. Here's an example with every directive enabled:

```
{:sources {:default :self
           :script ["https://trustedscripts.com" :unsafe-eval]
           :style ["*.styles.example.com"
                   "https://inlinestyles.info"
                   :unsafe-inline]
           :img "*"
           :connect "https:"
           :font "http://webfonts.biz"
           :object :none
           :media ["http://media.example.com"]
           :frame :self}
 :sandbox [:allow-forms
           :allow-scripts
           :allow-top-navigation]
 :report-uri "/some/report-uri"}
```

Mapping [CSP directives](http://content-security-policy.com/) to Clojure values is simple: special values like `'self'`, `'none'`, and `'unsafe-inline'` become keywords, while sources like `https:` `*.example.com` and `https://example.com` become strings.

To include the policy in outgoing responses, just include `add-content-security-policy` as a middleware wrapper:

    (ns my-great-webapp.core
      (:require [malcontent.core :refer [add-content-security-policy]]))

    (defroutes app-routes
      (GET "/" [] my-great-request-handler))
      
    (def app (-> routes
                 (add-content-security-policy)))

For help writing a good security policy, check out the resources below.
                 
## Installation
Include malcontent as a dependency in `project.clj`:

    [malcontent "0.1.0-SNAPSHOT"]

## Content Security Policy Resources
- [CSP Playground](http://www.cspplayground.com/) &mdash; interactive examples of common CSP violations and a policy validator.
- [HTML5 Rocks introduction to CSP](http://www.html5rocks.com/en/tutorials/security/content-security-policy/) &mdash; a helpful overview of CSP use cases.
- [CSP quick reference](http://content-security-policy.com/) &mdash; a CSP directive cheat sheet.
- [Cross-browser support](http://caniuse.com/contentsecuritypolicy) &mdash; the current state of cross-browser CSP support.
- [Policy recommendation bookmarklet](http://brandon.sternefamily.net/posts/2010/10/content-security-policy-recommendation-bookmarklet/) &mdash; a one-click tool that analyzes existing pages and generates a policy recommendation.
- [Automated policy generator](http://cspisawesome.com/) &mdash; create a valid policy online by clicking helpful checkboxes.
- [W3C CSP 1.0 specification](http://www.w3.org/TR/CSP/) &mdash; straight from the source.

## License
Malcontent is released under the Apache License, v2.0. For details, see the file `LICENSE.md` in this repository.
