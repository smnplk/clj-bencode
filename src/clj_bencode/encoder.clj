(ns clj-bencode.encoder
  (:import java.io.ByteArrayOutputStream))

(defn to-string [obj]
  (let [baos (ByteArrayOutputStream.)]
    (encode-obj obj baos)
    (.toString baos "UTF-8")))

(defn encode-obj [obj ^ByteArrayOutputStream s]
  (cond
    (string? obj) (encode-string obj s)
    (number? obj) (encode-number obj s)
    (vector? obj) (encode-list obj s)
    (map? obj) (encode-map obj s)))

(defn- encode-number [number ^ByteArrayOutputStream s]
  (let [number-string (str \i number \e)
        byte-array (.getBytes number-string "UTF-8")]
    (.write s byte-array 0 (.length number-string))))

(defn- encode-string [st ^ByteArrayOutputStream s]
  (let [new-str (str (.length st) \: st)
        byte-array (.getBytes new-str "UTF-8")]
    (.write s byte-array 0 (.length new-str))))

(defn- encode-list [v ^ByteArrayOutputStream s]
  (.write s (int \l))
  (doseq [obj v]
    (encode-obj obj s))
  (.write s (int \e)))

(defn- encode-map [m ^ByteArrayOutputStream s]
  (.write s (int \d))
  (doseq [obj (flatten (seq m))]
    (encode-obj obj s))
  (.write s (int \e)))
