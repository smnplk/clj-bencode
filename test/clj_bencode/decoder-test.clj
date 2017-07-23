(ns clj-bencode.core-test
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clj-bencode.decoder :as decoder]))

; HELPERS

(defmulti encode (fn [obj]
                   (cond
                     (string? obj) :string
                     (number? obj) :number)))

(defmethod encode :string [string]
  (str (.length string) \: string))

(defmethod encode :number [number]
  (str \i number \e))

(defmethod encode :default [obj]
  (throw (IllegalArgumentException. (str "Can't encode object of type " (type obj)))))

(defn encode-list [vector]
  (apply str (concat [\l] (map encode vector) [\e])))

; GENERATORS

; generator that generates a list of 2 element vectors, where first element is integer
; and second element  is  bencoded integer

(def int-and-encoded
  (gen/bind gen/int
            (fn [integer] (gen/tuple (gen/return integer) (gen/return (str \i integer \e))))))

; generator that generates a list of 2 element vectors, where first element is string
; and second lement is bencoded string

(def string-and-encoded
  (gen/bind gen/string
            (fn [string] (gen/tuple (gen/return string) (gen/return (str (.length string) \: string))))))

; generator that generates a list of 2 element vectors, where first element is a vector of integers
; and second lement is bencoded list of integers

(def list-of-ints-and-encoded
  (gen/bind (gen/vector gen/int)
            (fn [vector] (gen/tuple (gen/return vector) (gen/return (encode-list vector))))))

; PROPERTIES

(def can-decode-integers
  (prop/for-all [pair int-and-encoded]
                (let [[integer bencoded] pair]
                  (= integer (decoder/decode-from-string bencoded)))))

(tc/quick-check 1000 can-decode-integers)

(def can-decode-strings
  (prop/for-all [pair string-and-encoded]
                (let [[string bencoded] pair]
                  (= string (decoder/decode-from-string bencoded)))))

(tc/quick-check 1000 can-decode-strings)

(def can-decode-list-of-integers
  (prop/for-all [pair list-of-ints-and-encoded]
                (let [[vector encoded] pair]
                  (= vector (decoder/decode-from-string encoded)))))


(tc/quick-check 1000 can-decode-list-of-integers)


;(gen/sample (gen/vector encoded-int))
;(gen/sample encoded-string)
;(gen/sample int-and-encoded)
;(gen/sample string-and-encoded)
;(gen/sample list-of-ints-and-encoded)
