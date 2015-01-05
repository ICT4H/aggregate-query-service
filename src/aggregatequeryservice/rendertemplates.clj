(ns aggregatequeryservice.rendertemplates
  (:use aggregatequeryservice.utils)
  (:require [freemarker-clj.core :as ftl])
  (:import [freemarker.template
           Configuration]
           (freemarker.cache ClassTemplateLoader FileTemplateLoader MultiTemplateLoader TemplateLoader)
           (java.io File)))

(defn is-query-name [query-name query]
  (= query-name (:queryName query)))

(def get-results (comp :result filter-first))

(defn get-query-result [query-results query-to-render]
  (->> query-results
       (get-results (partial is-query-name query-to-render))
       (hash-map (keyword query-to-render))))

(defn render-template
  [ftl-config query-results {template-path :template_path query-name-list :query_list}]
  (->> query-name-list
       (map (partial get-query-result query-results))
       (apply merge)
       (ftl/render ftl-config template-path)))

(defn render-templates
  [template-list extra-params-map template-base-dir query-results]
  (let [class-loader (doto (new ClassTemplateLoader (class Configuration) "/"))
        file-loader (if (nil? template-base-dir)
                      (doto (new FileTemplateLoader))
                      (doto (new FileTemplateLoader (new File template-base-dir))))
        multi-template-loader (doto (new MultiTemplateLoader (into-array TemplateLoader [class-loader file-loader])))
        ftl-config (doto (ftl/gen-config :shared extra-params-map)
                     (.setTemplateLoader multi-template-loader))]
    (pmap (partial render-template ftl-config query-results) template-list)))