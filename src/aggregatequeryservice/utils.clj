(ns aggregatequeryservice.utils
  (:import (java.io FileNotFoundException)
           (java.net URL))
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]))


(def not-nil? (complement nil?))

(defn is-substring-of
  "Gives back a boolean output whether the first
  string is a substring of the second string or not"
  [sub-string string]
  (->> sub-string
       (re-pattern)
       (#(re-find % string))
       (not-nil?)))


(defn file-path [config-file]
  (try
    (.getPath (io/resource config-file))
    (catch FileNotFoundException f
      config-file)
    (catch NullPointerException n
      config-file)))

(def slurp-more (comp file-path slurp))

(defn read-config
  "Reads the config file from the file path given"
  [config-file]
  (json/read-str (slurp-more config-file) :key-fn keyword))

(defn is-of-size [size coll]
  (if (= (count coll) size)
    coll
    (throw (RuntimeException. (str "More/Less than " size " element(s) found")))))


(defn first-one
  [coll]
  (first (is-of-size 1 coll)))

(def filter-first (comp first filter))

(defn print-and-return [k]
  (println k)
  k)
