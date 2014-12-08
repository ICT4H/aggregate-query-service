(ns aggregate-query-service.core
  (:require [clojure.data.json :as json])
  (:gen-class))

(defn read-config
  [config-file]
  (json/read-str (slurp config-file) :key-fn keyword))