(ns aggregatequeryservice.postservice
  (:refer-clojure :exclude [await])
  (:require [aggregatequeryservice.runqueries :as aqs]
            [aggregatequeryservice.utils :as u]
            [aggregatequeryservice.dblog :as dblog]
            [aggregatequeryservice.rendertemplates :as rt]
            [http.async.client :as h])
  (:import (java.util.HashMap)
           (clojure.lang PersistentArrayMap))
  (:gen-class
    :name aggregatequeryservice.postservice
    :methods [#^{:static true} [executeQueriesAndPostResultsSync [String
                                                                  javax.sql.DataSource
                                                                  java.util.HashMap
                                                                  java.util.HashMap
                                                                  java.util.HashMap
                                                                  String] java.util.List]]))

(defn post-template [http-post-uri http-post-headers payload]
  (with-open [client (h/create-client)]
    (let [response (h/await (h/POST client http-post-uri :body payload :headers http-post-headers))
          status (:code (h/status response))
          result (h/string response)]
      {:status   status
       :response result})))

(defn run-queries-render-templates-post
  [aqs-config-path data-source query-params-map extra-params-map http-post-headers http-post-uri]
  (let [aqs-config-map (u/read-config-to-map aqs-config-path)
        {query-json-path :query_json_path template-list :template_query_map template-base-dir :template_base} aqs-config-map]
    (->> (aqs/run-queries-and-get-results query-json-path data-source query-params-map)
         (rt/render-templates template-list extra-params-map template-base-dir)
         (mapv (partial post-template http-post-uri http-post-headers)))))

(defn -executeQueriesAndPostResultsSync
  "Java exposed sync API"
  [aqs-config-path data-source query-params-map extra-params-map http-post-headers http-post-uri]
  (let [query-params-map (into {} query-params-map)
        extra-params-map (into {} extra-params-map)
        http-post-headers (into {} http-post-headers)
        task-id (dblog/insert data-source aqs-config-path "IN PROGRESS" (merge query-params-map extra-params-map))
        results (run-queries-render-templates-post aqs-config-path data-source query-params-map extra-params-map http-post-headers http-post-uri)]
    (dblog/update data-source task-id "DONE" results)
    results))
