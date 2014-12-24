(ns aggregatequeryservice.rendertemplates
  (:use aggregatequeryservice.utils)
  (:require [freemarker-clj.core :as ftl]))

(defn get-query-result [query-results query-to-render]
  (->> query-results
       (filter (fn [query]
                 (= query-to-render (get query :queryName ""))))
       (first)
       (#(get %1 :result {}))
       (first-one))
  )
(defn merge-query-results
  [get-query-result & query-results]
  (->> query-results
       (map get-query-result)
       (apply merge)))


(defn render-template
  [extra-params-map query-results template-map]
  (let [{template-path :template_path query-name-list :query_list} template-map
        template-config (ftl/gen-config :shared extra-params-map)
        render-template-partial (partial ftl/render template-config template-path)
        get-query-result-partial (partial get-query-result query-results)
        merge-query-results-partial (partial merge-query-results get-query-result-partial)]
    (->> query-name-list
         (reduce merge-query-results-partial)
         (render-template-partial))))

(defn render-templates
  [aqs-config-map extra-params-map query-results]
  (let [template-list (get aqs-config-map :template_query_map)]
    (map (partial render-template extra-params-map query-results) template-list))
  )