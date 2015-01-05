(ns aggregatequeryservice.postservice
  (:require [aggregatequeryservice.runqueries :refer :all :as aqs]
            [aggregatequeryservice.utils :refer :all :as u]
            [aggregatequeryservice.dblog :refer :all :as dblog]
            [aggregatequeryservice.rendertemplates :refer :all :as rt]
            [http.async.client :refer :all :as h])
  (:gen-class
  :name aggregatequeryservice.postservice
  :methods [#^{:static true} [executeQueriesAndPostResultsSync [String javax.sql.DataSource java.util.HashMap java.util.HashMap java.util.HashMap] Object]]))

(defn post-template [http-post-uri http-post-headers payload]
  (with-open [client (h/create-client)]
    (let [response (h/POST client http-post-uri :body payload :headers http-post-headers)]
      (-> response
          h/await
          h/string))))

(defn run-queries-render-templates-post
  [aqs-config-path data-source query-params-map extra-params-map http-post-headers]
  (let [aqs-config-map (u/read-config aqs-config-path)
        {query-json-path :query_json_path http-post-uri :http-post-uri template-list :template_query_map} aqs-config-map]
    (->> (aqs/run-queries-and-get-results query-json-path data-source query-params-map)
         (rt/render-templates template-list extra-params-map)
         (pmap (partial post-template http-post-uri http-post-headers)))))

(defn -executeQueriesAndPostResultsSync
  "Java exposed sync API"
  [aqs-config-path data-source query-params-map extra-params-map http-post-headers]
  (let [query-params-map (into {} query-params-map)
        extra-params-map (into {} extra-params-map)
        http-post-headers (into {} http-post-headers)
        task-id (dblog/insert-task aqs-config-path data-source "In Progress")]
    (run-queries-render-templates-post aqs-config-path data-source query-params-map extra-params-map http-post-headers)
    (dblog/update-task task-id data-source "Done")))