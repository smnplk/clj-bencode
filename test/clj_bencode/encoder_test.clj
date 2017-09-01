(ns clj-bencode.encoder-test
  (:require [clj-bencode.encoder :as encoder]
            [clojure.test.check :as tc]
            [clj-bencode.helpers :as helpers]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop])
  (:use clj-bencode.protocol :reload))

(def can-encode-integers
  (prop/for-all [i gen/int]
                (= (helpers/encode i) (bencode i))))


(def can-encode-strings
  (prop/for-all [s gen/string]
                (= (helpers/encode s) (bencode s))))


(def can-encode-list-of-integers
  (prop/for-all [int-list (gen/vector gen/int)]
                (= (helpers/encode int-list) (bencode int-list))))


(def can-encode-list-of-strings
  (prop/for-all [str-list (gen/vector gen/string)]
                (= (helpers/encode str-list) (bencode str-list))))


(def can-encode-map-of-string-to-string
  (prop/for-all [str-map (gen/map gen/string gen/string)]
                (= (helpers/encode str-map) (bencode str-map))))

(def can-encode-nested-maps
  (prop/for-all [m helpers/nested-map]
                (= (helpers/encode m) (bencode m))))


(defn run-encoding-tests [n]
  (tc/quick-check n can-encode-integers)
  (tc/quick-check n can-encode-strings)
  (tc/quick-check n can-encode-list-of-integers)
  (tc/quick-check n can-encode-list-of-strings)
  (tc/quick-check n can-encode-map-of-string-to-string)
  (tc/quick-check n can-encode-nested-maps))

;(run-encoding-tests 100)
