(ns clj-bencode.decoder-test
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clj-bencode.helpers :as helpers])
  (:use clj-bencode.protocol))


(def can-decode-integers
  (prop/for-all [i gen/int]
                (= i (bdecode (helpers/encode i)))))


(def can-decode-strings
  (prop/for-all [s gen/string]
                (= s (bdecode (helpers/encode s)))))


(def can-decode-list-of-integers
  (prop/for-all [int-list (gen/vector gen/int)]
                (= int-list (bdecode (helpers/encode int-list)))))


(def can-decode-list-of-strings
  (prop/for-all [str-list (gen/vector gen/string)]
                (= str-list (bdecode (helpers/encode str-list)))))

(def can-decode-map-of-string-to-string
  (prop/for-all [str-map (gen/map gen/string gen/string)]
                (= str-map (bdecode (helpers/encode str-map)))))

(def can-decode-nested-maps
  (prop/for-all [m helpers/nested-map]
                (= m (bdecode (helpers/encode m)))))


(defn run-decoding-tests [n]
  (tc/quick-check n can-decode-integers)
  (tc/quick-check n can-decode-strings)
  (tc/quick-check n can-decode-list-of-integers)
  (tc/quick-check n can-decode-list-of-strings)
  (tc/quick-check n can-decode-map-of-string-to-string)
  (tc/quick-check n can-decode-nested-maps))

;(run-decoding-tests 100)

;(gen/sample (gen/vector encoded-int))
;(gen/sample encoded-string)
;(gen/sample int-and-encoded)
;(gen/sample string-and-encoded)
;(gen/sample list-of-ints-and-encoded)
;(gen/sample list-of-strings-and-encoded)
;(gen/sample map-of-string-to-string)
;(gen/sample nested-map)
