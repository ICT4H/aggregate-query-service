(ns aggregate-query-service.core
  (:require [clojure.data.json :as json]
            [clojure.java.jdbc :as jdbc])
  (:gen-class))

(def not-nil? (complement nil?))

(defn is-substring-of
  [sub-string string]
  (->> sub-string
       (re-pattern)
       (#(re-find % string))
       (not-nil?)))

(defn read-config
  [config-file]
  (json/read-str (slurp config-file) :key-fn keyword))

(defn get-queries
  [query-group]
  (map (fn [query-map]
         (assoc query-map :queryGroupname (get query-group :queryGroupname)))
       (get query-group :queries []))
  )

(defn replace-param [query param-value param-name]
  (let [search-param (str ":" param-name ":")
        sql-query (get query :query)]
    (if (is-substring-of search-param sql-query)
      (assoc query :query (clojure.string/replace sql-query search-param param-value))
      query
      )))

(defn render-query
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
  [query-params-map query-list]
  (map (partial render-query query-params-map) query-list))

(defn run-queries-and-get-results
  [config-file data-source query-params-map]
  (->> (read-config config-file)
       (map get-queries)
       (render-queries query-params-map))
  )