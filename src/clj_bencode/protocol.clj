; don't see the need for protocols for now
(comment
(ns clj-bencode.protocol
  (:require [clj-bencode.decoder :as decoder]
            [clj-bencode.encoder :as encoder])
  (:import (java.io ByteArrayInputStream ByteArrayOutputStream)))

(defprotocol IDecode
  (bdecode [this]))

(defprotocol IEncode
  (bencode [this]))

(extend-type String
  IDecode
  (bdecode [this]
    (decoder/decode (ByteArrayInputStream. (.getBytes this))))

  IEncode
  (bencode [this]
    (encoder/to-string this)))

(extend-protocol IEncode
  java.lang.String
  (bencode [this]
    (encoder/to-string this))

  java.lang.Integer
  (bencode [this]
    (encoder/to-string this))

  clojure.lang.PersistentVector
  (bencode [this]
    (encoder/to-string this))

  clojure.lang.PersistentArrayMap
  (bencode [this]
    (encoder/to-string this)))

)
