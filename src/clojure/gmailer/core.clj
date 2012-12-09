
(ns gmailer.core
  (:import (com.google.code.samples.oauth2 OAuth2SaslClientFactory OAuth2Authenticator)
           (java.util Properties)
           (javax.mail Session Folder)
           (com.sun.mail.imap IMAPSSLStore)))

(OAuth2Authenticator/initialize)

(def GMAIL_IMAP_HOST "imap.gmail.com")
(def GMAIL_IMAP_PORT 993)

(def ^{:dynamic true} *token* nil)
(def ^{:dynamic true} *email* nil)
(def ^{:dynamic true} *store* nil)

(defn ^ {:doc "Returns the Properties object for configuring
  an IMAP session."}
  imap-properties []
  (let [props (Properties.)]
     (doto props
      (.put "mail.imaps.sasl.enable" "true")
      (.put "mail.imaps.sasl.mechanisms", "XOAUTH2")
      (.put OAuth2SaslClientFactory/OAUTH_TOKEN_PROP *token*)) 
    props))

(defn ^{:doc "Returns an IMAP store that can be used to fetch
  messages from Gmail."}
  imap-store []
  (let [props (imap-properties)
        session (Session/getInstance props)
        store (IMAPSSLStore. session nil)]
    (.connect store GMAIL_IMAP_HOST GMAIL_IMAP_PORT *email* "")
    store))

(defn to-message [msg]
  {:subject (.getSubject msg)})

;; Public
;; ------

(defmacro with-credentials [email token & body] 
  `(binding [*email* ~email
             *token* ~token]
    (doall ~@body)))

(defmacro with-store [store & body]
  `(binding [*store* ~store]
     (doall ~@body)))

(defn inbox []
  (let [folder (.getFolder *store* "Inbox")]
    (.open folder (Folder/READ_ONLY))
    (map to-message (.getMessages folder))))

