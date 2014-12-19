(ns aggregate-query-service.core
  (:require [clojure.data.json :as json]
            [clojure.java.jdbc :as jdbc])
  (:gen-class))

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

(defn get-queries
  "Appends query group name to every query object"
  [query-group]
  (map (fn [query-map]
         (assoc query-map :queryGroupname (get query-group :queryGroupname)))
       (get query-group :queries []))
  )

(defn replace-param
  "Replace parameter name with parameter value in the SQL query."
  [query param-value param-name]
  (let [search-param (str ":" param-name ":")
        sql-query (get query :query)]
    (if (is-substring-of search-param sql-query)
      (assoc query :query (clojure.string/replace sql-query search-param param-value))
      query
      )))

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
    (assoc query :result (jdbc/db-query-with-resultset db-spec [sql-query] identity))
    ))

(defn run-queries-and-get-results
  "Takes in the path to the configuration file, data source and a hash map of parameters for
  the SQL to completely render it."
  [config-file data-source query-params-map]
  (->> (read-config config-file)
       (map get-queries)
       (flatten)
       (render-queries query-params-map)
       (pmap (partial fire-query data-source)))
  )