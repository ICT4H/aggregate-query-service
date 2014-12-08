(ns aggregate-query-service.core
  (:require [clojure.data.json :as json])
  (:gen-class))

(defn read-config
  [config-file]
  (json/read-str (slurp config-file) :key-fn keyword))

(defn construct-sql
  [{query :query start-date :start-date end-date :end-date}]
  (clojure.string/replace (clojure.string/replace query #"startDate" start-date) #"endDate" end-date)
  )

(defn fire-query
  [query]
  ((construct-sql query)))

(defn fire-queries
  [{queries :queries start-date :start-date end-date :end-date}]
  (pmap fire-query (map (fn [query] (assoc query :start-date start-date :end-date end-date)) queries)))

(defn execute-all-queries
  [config-file start-date end-date]
  (pmap fire-queries (assoc (read-config config-file) :start-date start-date :end-date end-date)))