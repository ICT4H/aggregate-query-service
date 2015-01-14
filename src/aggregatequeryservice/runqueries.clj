(ns aggregatequeryservice.runqueries
  (:require [clojure.java.jdbc :as jdbc]
            [aggregatequeryservice.dblog :as dblog]
            [cheshire.core :refer :all])
  (:use aggregatequeryservice.utils)
  (:gen-class
  :name aggregatequeryservice.runqueries
  :methods [#^{:static true} [AQS [String javax.sql.DataSource java.util.HashMap] Object]]))

(defn get-queries
  "Appends query group name to every query object"
  [query-group]
  (map (fn [query-map]
         (assoc query-map :queryGroupname (get query-group :queryGroupname)))
       (get query-group :queries [])))

(defn replace-param
  "Replace parameter name with parameter value in the SQL query."
  [query param-value param-name]
  (let [search-param (str ":" param-name ":")
        sql-query (get query :query)]
    (if (is-substring-of search-param sql-query)
      (assoc query :query (clojure.string/replace sql-query search-param param-value))
      query)))

(defn render-query
  "Takes in a map of query parameters and replaces all the parameters if they exist in the
  query with the appropriate value"
  [query-params-map query]
  (loop [query-params-map query-params-map
         query query]
    (if (empty? query-params-map)
      query
      (let [key (ffirst query-params-map)
            reduced-map (dissoc query-params-map key)
            param-value (get query-params-map key)
            param-name (name key)]
        (recur reduced-map (replace-param query param-value param-name))))))

(defn render-queries
  "Calls render query on each every query from the query list"
  [query-params-map query-list]
  (map (partial render-query query-params-map) query-list))

(defn fire-query
  "Hits the database with SQL queries."
  [data-source query]
  (let [db-spec {:datasource data-source}
        sql-query (get query :query)]
    (->> (jdbc/query db-spec [sql-query])
         (assoc query :result))))

(defn run-queries-and-get-results
  "Takes in the path to the configuration file, data source and a hash map of parameters for
  the SQL to completely render it."
  [config-file data-source query-params-map]
  (->> (read-config-to-map config-file)
       (map get-queries)
       (flatten)
       (render-queries query-params-map)
       (pmap (partial fire-query data-source))))

(defn -AQS
  [config-file data-source query-params-map]
  (let [query-params-map (into {} query-params-map)
        task-id (dblog/insert data-source config-file "IN PROGRESS" query-params-map)
        task-future (future
                      (let [results (run-queries-and-get-results config-file data-source query-params-map)]
                        (dblog/update data-source task-id "DONE" (generate-string results))
                        (generate-string results)))]
    {:results task-future :task_id task-id}))