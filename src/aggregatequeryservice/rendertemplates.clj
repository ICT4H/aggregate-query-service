(ns aggregatequeryservice.rendertemplates
  (:use aggregatequeryservice.utils)
  (:require [freemarker-clj.core :as ftl]))

(defn is-query-name [query-name query]
  (= query-name (:queryName query)))

(def get-first-result (comp first-one :result filter-first))

(defn get-query-result [query-results query-to-render]
  (get-first-result (partial is-query-name query-to-render) query-results))

(defn render-template
  [ftl-config query-results {template-path :template_path query-name-list :query_list}]
  (->> query-name-list
       (map (partial get-query-result query-results))
       (apply merge)
       (ftl/render ftl-config template-path)))

(defn render-templates
  [template-list extra-params-map query-results]
  (let [ftl-config (ftl/gen-config :shared extra-params-map)]
    (pmap (partial render-template ftl-config query-results) template-list))
  )