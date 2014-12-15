(ns aggregate-query-service.core
  (:require [clojure.data.json :as json]
            [clojure.java.jdbc :as jdbc])
  (:gen-class))

(defn read-config
  [config-file]
  (json/read-str (slurp config-file) :key-fn keyword))

(defn get-queries
  [query-group]
  (map (fn [query-map]
         (assoc query-map :queryGroupname (get query-group :queryGroupname)))
       (get query-group :queries []))
  )

(defn generate-queries
  [query-params-map query-list]
  
  )

(defn run-queries-and-get-results
  [config-file data-source query-params-map]
  (->> (read-config config-file)
      (map get-queries)
      (flatten)
      (generate-queries query-params-map))
  )