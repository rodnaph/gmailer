
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

(defn ^{:doc "Returns the Properties object for configuring an IMAP session."}
  imap-properties []
  (let [props (Properties.)]
     (doto props
      (.put "mail.imaps.sasl.enable" "true")
      (.put "mail.imaps.sasl.mechanisms", "XOAUTH2")
      (.put OAuth2SaslClientFactory/OAUTH_TOKEN_PROP *token*)) 
    props))

(defn ^{:doc "Returns an IMAP store that can be used to fetch messages from Gmail."}
  imap-store []
  (let [props (imap-properties)
        session (Session/getInstance props)
        store (IMAPSSLStore. session nil)]
    (.connect store GMAIL_IMAP_HOST GMAIL_IMAP_PORT *email* "")
    store))

(defn ^{:doc "Creates a function to read the content of a message"}
  content-reader [stream]
  (fn [] ""))

(defn ^ {:doc "Converts an IMAPAddress to a map"}
  email2map [email]
  {:address (.toString email)})

(defn ^ {:doc "Converts an IMAPMessage to a map"}
  message2map [msg]
  {:subject (.getSubject msg)
   :content-type (.getContentType msg)
   :content-fn (content-reader (.getMimeStream msg))
   :encoding (.getEncoding msg)
   :from (email2map (first (.getFrom msg))) 
   :message-id (.getMessageID msg)
   :date-received (.getReceivedDate msg)
   :reply-to (email2map (first (.getReplyTo msg))) 
   :sender (email2map (.getSender msg)) 
   :date-sent (.getSentDate msg)
   :size (.getSize msg)})

;; Public
;; ------

(defmacro with-credentials [email token & body] 
  `(binding [*email* ~email
             *token* ~token]
    (doall ~@body)))

(defmacro with-store [store & body]
  `(binding [*store* ~store]
     (doall ~@body)))

(defn ^ {:doc "Return the Gmail inbox"}
  inbox []
  (let [folder (.getFolder *store* "Inbox")]
    (.open folder (Folder/READ_ONLY))
    (map message2map (.getMessages folder))))

(defn ^ {:doc "Search Gmail for the given term, which can include all supported Gmail features"}
  search [term])

