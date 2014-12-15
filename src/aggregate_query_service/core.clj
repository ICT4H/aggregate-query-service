(ns aggregate-query-service.core
  (:require [clojure.data.json :as json]
            [clojure.java.jdbc :as jdbc])
  (:gen-class))

(def not-nil? (complement nil?))

(defn is-substring-of
  [sub-string string]
  (not-nil? (re-find (re-pattern sub-string) string)))

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
      )))

(defn render-query
  [query-params-map query]
  (filter not-nil? (map #(replace-param query (get query-params-map %1) (name %1)) (keys query-params-map))))


(defn render-queries
  [query-params-map query-list]
  (flatten (map #(render-query query-params-map %1) query-list))
  )

(defn run-queries-and-get-results
  [config-file data-source query-params-map]
  (->> (read-config config-file)
       (map get-queries)
       (flatten)
       (render-queries query-params-map))
  )