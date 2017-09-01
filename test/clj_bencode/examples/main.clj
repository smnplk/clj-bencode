(ns clj-bencode.examples.main
  (:import java.io.File)
  (:use [clj-bencode.protocol]))

(def project-path (apply str (butlast (.getAbsolutePath (new File ".")))))
(def sample-torrent-path (str project-path "test/clj_bencode/examples/files/sample.torrent"))

sample-torrent-path

(bdecode-file sample-torrent-path)
