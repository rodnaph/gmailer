
Gmailer
=======

Gmailer is a Clojure library for using Gmail IMAP/SMTP via OAuth.

Installation
------------

Gmailer is available from Clojars, so just add it as a dependency of your project.

*DISCLAIMER:* This is currently a README driven development work in progress, so
nothing apart from the actual authentication really works yet.

```clojure
:dependencies [[gmailer "0.0.1"]]
```

Usage
-----

Gmailer assumes you already have a valid OAuth access token.  Obtaining this
access token is outside the scope of this library.  When you have the token
though, it's easy to use:

```clojure
(ns my.project
  (:require [gmailer.core as g]))

(def store (g/with-credentials "my.user@gmail.com" "ACCESS_TOKEN_HERE"
             (g/imap-store)))

(g/with-store store
  (println (take 5 (g/inbox))))
```

All access should be wrapped in the _with-credentials_ macro.

