(ns clj-bencode.util
  (:import java.io.BufferedReader))

(defn digit?
  "Returns whether byte b is a digit, e.g., value between 0 and 9."
  [b]
  (and (>= b 48) (<= b 57)))


(defn read-and-return
  "Read a byte from the stream, put it back on the strem and return it"
  [^BufferedReader r]
  (do
    (.mark r 1)
    (let [byte (.read r)]
      (.reset r)
      byte)))
