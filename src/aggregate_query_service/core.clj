(ns aggregate-query-service.core
  (:require [clojure.data.json :as json]
            [clojure.java.jdbc :as jdbc])
  (:gen-class))

(defn execute-sql
  [query]
  ((jdbc/get-connection {:datasource data-source} )))

(defn read-config
  [config-file]
  (json/read-str (slurp config-file) :key-fn keyword))

(defn construct-sql
  [{query :query start-date :start-date end-date :end-date}]
  (clojure.string/replace (clojure.string/replace query #"startDate" start-date) #"endDate" end-date)
  )

(defn fire-query
  [query]
  (execute-sql (construct-sql query)))


(defn fire-queries
  [{queries :queries start-date :start-date end-date :end-date}]
  (pmap fire-query (map (fn [query] (assoc query :start-date start-date :end-date end-date)) queries)))

(defn execute-all-queries
  [config-file start-date end-date data-source]
  (pmap fire-queries (assoc (read-config config-file) :start-date start-date :end-date end-date)))