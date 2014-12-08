(ns aggregate-query-service.core
  (:require [clojure.data.json :as json])
  (:gen-class))

(defn read-config
  [config-file]
  (json/read-str (slurp config-file) :key-fn keyword))

(defn fire-query
  [{query :query}]
  (println query))

(defn fire-queries
  [{queries :queries}]
  (map fire-query queries)
  )

(defn execute-all-queries
  [config-file]
  (map fire-queries (read-config config-file)))