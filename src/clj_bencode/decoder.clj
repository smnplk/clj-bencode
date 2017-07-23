(ns clj-bencode.decoder
  (:import (java.io InputStream InputStreamReader ByteArrayInputStream)))


(declare decode decode-string decode-number decode-list decode-dict)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; PUBLIC INTERFACE ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn decode-from-string
  "Decodes bencoded strings"
  [string]
  (decode (ByteArrayInputStream. (.getBytes string "UTF-8"))))


(defn decode [^InputStream s]
  (let [next (read-and-return s) ch (char next)]
    (cond
      (digit? next) (decode-string s)
      (= ch \i) (do (skip s) (decode-number s \e))
      (= ch \l) (do (skip s) (decode-list s))
      (= ch \d) (do (skip s) (decode-dict s)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; PRIVATE FUNCTIONS ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- digit?
  "Returns whether byte b is a digit, e.g., value between 0 and 9."
  [b]
  (and (>= b 48) (<= b 57)))


(defn- read-and-return
  "Read a byte from the stream, put it back on the strem and return the byte"
  [^InputStream s]
  (do
    (.mark s 1)
    (let [byte (.read s)]
      (.reset s)
      byte)))


(defn- skip
  "Just consume one byte from the stream"
  [^InputStream s]
  (.read s))


(defn- decode-number [^InputStream s delim]
  (loop [next (.read s) result ""]
    (if (neg? next)
      (throw (ex-info "Number not terminated correctly" {:number result :expected-delim delim}))
      (let [ch (char next)]
        (if (= ch delim)
          (BigInteger. result)
          (recur (.read s) (str result ch)))))))


(defn- decode-string [^InputStream s]
  (let [len (decode-number s \:)
        isr (InputStreamReader. s "UTF-8")
        buffer (char-array len)]
    (.read isr buffer)
    (String/valueOf buffer)))


(defn- decode-list [^InputStream s]
  (loop [result []]
    (let [next (read-and-return s)]
      (if (= (char next) \e)
        result
        (recur (conj result (decode s)))))))


(defn- decode-dict [^InputStream s]
  (apply hash-map (decode-list s)))
