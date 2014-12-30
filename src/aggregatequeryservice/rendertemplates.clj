(ns aggregatequeryservice.rendertemplates
  (:use aggregatequeryservice.utils)
  (:require [freemarker-clj.core :as ftl]))

(defn get-query-result [query-results query-to-render]
  (->> query-results
       (filter (fn [query]
                 (= query-to-render (get query :queryName))))
       (first)
       (#(get %1 :result {}))
       (first-one))
  )

(defn render-template
  [extra-params-map query-results {template-path :template_path query-name-list :query_list}]
  (->> query-name-list
       (map (partial get-query-result query-results))
       (apply merge)
       (ftl/render (ftl/gen-config :shared extra-params-map) template-path)))

(defn render-templates
  [aqs-config-map extra-params-map query-results]
  (let [template-list (get aqs-config-map :template_query_map)]
    (pmap (partial render-template extra-params-map query-results) template-list))
  )