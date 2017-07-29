(ns clj-bencode.protocol
  (:require [clj-bencode.decoder :as decoder]
            [clj-bencode.encoder :as encoder])
  (:import (java.io BufferedReader InputStreamReader ByteArrayInputStream)))

(defprotocol IDecode
  (bdecode [this]))

(defprotocol IEncode
  (bencode [this] [this file-path]))

(extend-type String
  IDecode
  (bdecode [this]
    (decoder/decode (BufferedReader. (InputStreamReader. (ByteArrayInputStream. (.getBytes this)))))))

(def base-encoder-impl
  {:bencode (fn
              ([this]
               (encoder/to-string this))
              ([this file-path]
               (encoder/to-file this file-path)))})

(extend java.lang.String
  IEncode
  base-encoder-impl)

(extend java.lang.Integer
  IEncode
  base-encoder-impl)

(extend java.lang.Long
  IEncode
  base-encoder-impl)

(extend clojure.lang.PersistentVector
  IEncode
  base-encoder-impl)

(extend clojure.lang.PersistentArrayMap
  IEncode
  base-encoder-impl)
