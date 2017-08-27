(ns clj-bencode.decoder
  (:require [clj-bencode.util :as util])
  (:import (java.io InputStreamReader ByteArrayInputStream BufferedReader)))

(declare decode decode-number decode-string decode-list decode-dict)


(defn decode-file [path]
  (let [reader (clojure.java.io/reader path)]
    (decode reader)))


(defn decode [^BufferedReader r]
  (let [next (util/read-and-return r) ch (char next)]
    (cond
      (util/digit? next) (decode-string r)
      (= ch \i) (do (.skip r 1) (decode-number r \e))
      (= ch \l) (do (.skip r 1) (decode-list r))
      (= ch \d) (do (.skip r 1) (decode-dict r)))))


(defn- decode-number [^BufferedReader r delim]
  (loop [next (.read r) result ""]
    (if (neg? next)
      (throw (ex-info "Number not terminated correctly" {:number result :expected-delim delim}))
      (let [ch (char next)]
        (if (= ch delim)
          (BigInteger. result)
          (recur (.read r) (str result ch)))))))


(defn- decode-string [^BufferedReader r]
  (let [len (decode-number r \:)
        buffer (char-array len)]
    (.read r buffer)
    (String/valueOf buffer)))


(defn- decode-list [^BufferedReader r]
  (loop [result []]
    (let [next (util/read-and-return r)]
      (if (or (neg? next) (= (char next) \e))
        (do
          (.skip r 1)
          result)
        (recur (conj result (decode r)))))))


(defn- decode-dict [^BufferedReader r]
  (apply hash-map (decode-list r)))
