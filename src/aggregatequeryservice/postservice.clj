(ns aggregatequeryservice.postservice
  (:use aggregatequeryservice.utils)
  (:require [aggregatequeryservice.runqueries :refer :all :as aqs]
            [aggregatequeryservice.rendertemplates :refer :all :as rt]))

(defn run-queries-render-templates-post
  [aqs-config-path data-source query-params-map extra-params-map http-post-headers]
  (let [aqs-config-map (read-config aqs-config-path)
        query-json-path (get aqs-config-map :query_json_path)]
    (->> (aqs/run-queries-and-get-results query-json-path data-source query-params-map)
         (rt/render-templates aqs-config-map extra-params-map))))
