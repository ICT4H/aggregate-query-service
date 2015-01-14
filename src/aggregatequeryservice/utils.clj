(ns aggregatequeryservice.utils
  (:import (java.io FileNotFoundException))
  (:require [cheshire.core :refer :all]
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

(defn read-config [config-file]
  (try
    (slurp (io/input-stream (io/resource config-file)))
    (catch FileNotFoundException f
      (slurp config-file))
    (catch Exception n
      (slurp config-file))))

(defn read-config-to-map
  "Reads the config file from the file path given"
  [config-file]
  (let [file-read (read-config config-file)]
    (parse-string file-read true)))

(def filter-first (comp first filter))

(defn print-and-return [k]
  (println k)
  k)
