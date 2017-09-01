(ns clj-bencode.helpers
  (:require [clojure.test.check.generators :as gen]))


; Recursive generator for nested maps, keys are always strings, but values can be integers, strings,
; vectors (of ints or strings ) or maps

(def compound-gen-map (fn [inner-gen]
                        (gen/map (gen/not-empty  gen/string) inner-gen)))

(def values (gen/one-of [gen/string gen/int
                         (gen/vector (gen/one-of [(gen/not-empty gen/string) gen/int]))]))

(def nested-map (gen/recursive-gen compound-gen-map values))


(declare encode-map encode-list)

(defmulti encode (fn [obj]
                   (cond
                     (string? obj)      :string
                     (number? obj)      :number
                     (map? obj)         :map
                     (sequential? obj)  :vector)))


(defmethod encode :string [string]
  (str (.length string) \: string))


(defmethod encode :number [number]
  (str \i number \e))


(defmethod encode :map [m]
  (encode-map m))


(defmethod encode :vector [v]
  (encode-list v))


(defmethod encode :default [obj]
  (throw (IllegalArgumentException. (str "Can't encode object of " (class obj)))))


(defn encode-list [v]
  (apply str (concat [\l] (mapv encode v) [\e])))


(defn encode-map [m]
  (let [v (apply concat (into [] m))]
    (apply str (concat [\d] (mapv encode v) [\e]))))


