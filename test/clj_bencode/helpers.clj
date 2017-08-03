(ns clj-bencode.helpers)

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
