(ns clj-bencode.encoder
  (:import (java.io Writer StringWriter)))

(declare encode-obj encode-string encode-number encode-map encode-list)

(defn to-string [obj]
  (let [w (StringWriter.)]
    (encode-obj obj w)
    (.toString  w)))


(defn to-file [obj file-path]
  (let [file (clojure.java.io/file file-path)]
    (.createNewFile file)
    (with-open [w (clojure.java.io/writer file)]
      (encode-obj obj w))))


(defn encode-obj [obj ^Writer w]
  (cond
    (string? obj) (encode-string obj w)
    (number? obj) (encode-number obj w)
    (vector? obj) (encode-list obj w)
    (map? obj) (encode-map obj w)))


(defn- encode-number [number ^Writer w]
  (let [number-string (str \i number \e)
        ch-array (.toCharArray number-string)]
    (.write w ch-array 0 (.length number-string))))


(defn- encode-string [st ^Writer w]
  (let [new-str (str (.length st) \: st)
        ch-array (.toCharArray new-str)]
    (.write w ch-array 0 (.length new-str))))


(defn- encode-list [v ^Writer w]
  (.write w (int \l))
  (doseq [obj v]
    (encode-obj obj w))
  (.write w (int \e)))


(defn- encode-map [m ^Writer w]
  (.write w (int \d))
  (doseq [[k v] (seq m)]
    (encode-obj k w)
    (encode-obj v w))
  (.write w (int \e)))
