(ns aggregatequeryservice.postservice
  (:require [aggregatequeryservice.runqueries :refer :all :as aqs]
            [aggregatequeryservice.utils :refer :all :as u]
            [aggregatequeryservice.rendertemplates :refer :all :as rt]
            [http.async.client :refer :all :as h])
  (:gen-class
  :name aggregatequeryservice.postservice
  :methods [#^{:static true} [renderftlandpost [String javax.sql.DataSource java.util.HashMap java.util.HashMap java.util.HashMap] String]]))

(defn post-template [aqs-config-map http-post-headers jsondata]
  (let [http-post-uri (get aqs-config-map :http-post-uri)
        jsondata (apply str jsondata)]
    (with-open [client (h/create-client)]                   ; Create client
      (let [resp (h/POST client http-post-uri :body jsondata :headers http-post-headers)]
        (h/await resp)
        (h/string resp)))))

(defn run-queries-render-templates-post
  [aqs-config-path data-source query-params-map extra-params-map http-post-headers]
  (let [aqs-config-map (u/read-config aqs-config-path)
        query-json-path (get aqs-config-map :query_json_path)]
    (->> (aqs/run-queries-and-get-results query-json-path data-source query-params-map)
         (rt/render-templates aqs-config-map extra-params-map)
         (post-template aqs-config-map http-post-headers))))

(defn -renderftlandpost
  [aqs-config-path data-source query-params-map extra-params-map http-post-headers]
  (let [query-params-map (into {} query-params-map)]
    [extra-params-map (into {} extra-params-map)]
    [http-post-headers (into {} http-post-headers)]
  (run-queries-render-templates-post aqs-config-path data-source query-params-map extra-params-map http-post-headers)))