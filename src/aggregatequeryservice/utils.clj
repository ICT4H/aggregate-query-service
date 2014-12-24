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