(ns aggregatequeryservice.rendertemplates
  (:use aggregatequeryservice.utils)
  (:require [freemarker-clj.core :as ftl]))

(defn get-query-result [query-results query-to-render]
  (->> query-results
       (filter-first (fn [query]
                       (= query-to-render (get query :queryName))))
       (:result)
       (first-one))
  )

(defn render-template
  [ftl-config query-results {template-path :template_path query-name-list :query_list}]
  (->> query-name-list
       (map (partial get-query-result query-results))
       (apply merge)
       (ftl/render ftl-config template-path)))

(defn render-templates
  [aqs-config-map extra-params-map query-results]
  (let [template-list (get aqs-config-map :template_query_map)
        ftl-config (ftl/gen-config :shared extra-params-map)]
    (pmap (partial render-template ftl-config query-results) template-list))
  )