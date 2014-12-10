(ns aggregate-query-service.core
  (:require [clojure.data.json :as json]
            [clojure.java.jdbc :as jdbc])
  (:gen-class))

;(defn execute-sql
;  [data-source query]
;  ((jdbc/get-connection {:datasource data-source})))
;
;(defn execute-sql-fn [data-source] (partial execute-sql data-source))
;
;(defn read-config
;  [config-file]
;  (json/read-str (slurp config-file) :key-fn keyword))
;
;(defn construct-sql
;  [{query :query start-date :start-date end-date :end-date}]
;  (clojure.string/replace (clojure.string/replace query #"startDate" start-date) #"endDate" end-date)
;  )
;
;(def fire-query (comp execute-sql construct-sql))
;
;(defn fire-query
;  [data-source query]
;  (execute-sql data-source (construct-sql query)))
;
;
;(defn fire-queries
;  [{queries :queries} query-executor start-date end-date]
;  (pmap query-executor (map (fn [query] (assoc query :start-date start-date :end-date end-date)) queries)))
;
;(defn execute-all-queries
;  [config-file start-date end-date data-source]
;  (let [file-config (read-config config-file)]
;    (pmap #(fire-queries (execute-sql-fn data-source) data-source start-date end-date) file-config)))
;
(defn execute-sql-query
  [data-source params query]
  (let [db-connection (jdbc/get-connection {:datasource data-source})]
    (jdbc/db-do-prepared db-connection query params)))

(defn sql-executor
  [data-source & query-params]
  (partial execute-sql-query data-source query-params))




