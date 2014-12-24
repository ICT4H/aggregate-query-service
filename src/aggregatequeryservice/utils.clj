(ns aggregatequeryservice.utils
  (:require [clojure.data.json :as json]))


(def not-nil? (complement nil?))

(defn is-substring-of
  "Gives back a boolean output whether the first
  string is a substring of the second string or not"
  [sub-string string]
  (->> sub-string
       (re-pattern)
       (#(re-find % string))
       (not-nil?)))

(defn read-config
  "Reads the config file from the file path given"
  [config-file]
  (json/read-str (slurp config-file) :key-fn keyword))

(defn is-of-size [size coll]
  (if (= (count coll) size)
    coll
    (throw (RuntimeException. (str "More/Less than " size " element(s) found")))))


(defn first-one
  [coll]
  (first (is-of-size 1 coll)))