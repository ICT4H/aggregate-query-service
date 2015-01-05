(ns aggregatequeryservice.rendertemplates-test
  (:use [midje.sweet])
  (:require [aggregatequeryservice.rendertemplates :refer :all :as rt]
            [freemarker-clj.core :as ftl]))

(def extra-params (hash-map
                    :dataset "Rendered Dataset"
                    :period "20141111"
                    :organization "71345684"
                    ))

(def template-map1 {
                    :template_path "resources/ftl/template1.ftl"
                    :query_list    '("Query_19")
                    })

(def template-map2 {
                    :template_path "resources/ftl/template2.ftl"
                    :query_list    '("Query_19" "Query_20")
                    })

(def query-results '({:result         ({:name "Some Name", :id 1} {:id 15, :name "Some First Name"}),
                      :queryGroupname "Query Group 1", :queryName "Query_1", :query "select * from something;"}
                      {:result         ({:name "Some Other Name", :id 2}),
                       :queryGroupname "Query Group 1", :queryName "Query_2", :query "select * from something_else;"}
                      {:result         ({:name "One More Name", :id 6}),
                       :queryGroupname "Query Group 2", :queryName "Query_3", :query "select * from one_more_thing;"}
                      {:result         ({:name "Another Name", :id 4}),
                       :queryGroupname "Query Group 2", :queryName "Query_2", :query "select * from another_thing;"}
                      {:result         ({:name "Some Random Name", :id 99}),
                       :queryGroupname "Query Group Random", :queryName "Query_2", :query "select * from random;"}
                      {:result         ({:v1 "Rendered v1", :v3 "Rendered v3", :v6 "Rendered v6"}),
                       :queryGroupname "Query Group Actual", :queryName "Query_19", :query "select * from some_table;"}
                      {:result         ({:v2 "Rendered v2", :v4 "Rendered v4", :v5 "Rendered v5"}),
                       :queryGroupname "Query Group Actual", :queryName "Query_20", :query "select * from some_other_table;"}))



(def ftl-config (ftl/gen-config :shared extra-params))
(defn set= [& vectors] (apply = (map set vectors)))

(facts "Get query result according to name"
       (fact "Return the only query result by query name"
             (rt/get-query-result query-results "Query_3")
             =>
             {:Query_3 '({:id 6, :name "One More Name"})})
       (fact "Take first one if multiple query results with the same name are found"
             (rt/get-query-result query-results "Query_2")
             =>
             {:Query_2 '({:id 2, :name "Some Other Name"})}))

(facts "Render template with query results"
       (fact "Render ftl with one query result"
             (clojure.data.json/read-str (rt/render-template ftl-config query-results template-map1))
             =>
             (clojure.data.json/read-str "{\"dataSet\":\"Rendered Dataset\",
                   \"orgUnit\":\"71345684\",
                   \"period\":\"20141111\",
                   \"dataValues\":[{\"dataElement\":\"AiPqHCbJQJ1\",\"categoryOptionCombo\":\"u2QXNMacZLt\",\"value\":\"Rendered v1\"},{\"dataElement\":\"AiPqHCbJQJ1\",\"categoryOptionCombo\":\"UBdaznQ8DlT\",\"value\":\"Rendered v3\"},{\"dataElement\":\"AiPqHCbJQJ2\",\"categoryOptionCombo\":\"KahybAysMCQ\",\"value\":\"Rendered v6\"}]}"))
       (fact "Render ftl with multiple query results"
             (set= (get (clojure.data.json/read-str (rt/render-template ftl-config query-results template-map2)) "dataValues")
                   (get (clojure.data.json/read-str "{\"dataSet\":\"Rendered Dataset\",\n\"orgUnit\":\"71345684\",\n\"period\":\"20141111\",\n\"dataValues\":[{\"dataElement\":\"AiPqHCbJQJ1\",\"categoryOptionCombo\":\"u2QXNMacZLt\",\"value\":\"Rendered v1\"},{\"dataElement\":\"AiPqHCbJQJ1\",\"categoryOptionCombo\":\"DA2N93v7s0O\",\"value\":\"Rendered v2\"},{\"dataElement\":\"AiPqHCbJQJ1\",\"categoryOptionCombo\":\"UBdaznQ8DlT\",\"value\":\"Rendered v3\"},{\"dataElement\":\"AiPqHCbJQJ2\",\"categoryOptionCombo\":\"tSwmrlTW11V\",\"value\":\"Rendered v4\"},{\"dataElement\":\"AiPqHCbJQJ2\",\"categoryOptionCombo\":\"GYRYyntlK7n\",\"value\":\"Rendered v5\"},{\"dataElement\":\"AiPqHCbJQJ2\",\"categoryOptionCombo\":\"KahybAysMCQ\",\"value\":\"Rendered v6\"}]}") "dataValues"))
             =>
             true))

(facts "Render template with query results"
       (fact "Render ftl with multiple template maps"
             (let [expected (clojure.data.json/read-str "[{\n    \"dataSet\": \"Rendered Dataset\",\n    \"period\": \"20141111\",\n    \"orgUnit\": \"71345684\",\n    \"dataValues\": [\n    {\n    \"dataElement\": \"AiPqHCbJQJ1\",\n    \"categoryOptionCombo\": \"u2QXNMacZLt\",\n    \"value\": \"Rendered v1\"\n    },\n    {\n    \"dataElement\": \"AiPqHCbJQJ1\",\n    \"categoryOptionCombo\": \"UBdaznQ8DlT\",\n    \"value\": \"Rendered v3\"\n    },\n    {\n    \"dataElement\": \"AiPqHCbJQJ2\",\n    \"categoryOptionCombo\": \"KahybAysMCQ\",\n    \"value\": \"Rendered v6\"\n    }\n]\n}, {\n  \"dataSet\": \"Rendered Dataset\",\n  \"period\": \"20141111\",\n  \"orgUnit\": \"71345684\",\n  \"dataValues\": [\n    {\n      \"dataElement\": \"AiPqHCbJQJ1\",\n      \"categoryOptionCombo\": \"u2QXNMacZLt\",\n      \"value\": \"Rendered v1\"\n    },\n    {\n      \"dataElement\": \"AiPqHCbJQJ1\",\n      \"categoryOptionCombo\": \"UBdaznQ8DlT\",\n      \"value\": \"Rendered v3\"\n    },\n    {\n      \"dataElement\": \"AiPqHCbJQJ2\",\n      \"categoryOptionCombo\": \"KahybAysMCQ\",\n      \"value\": \"Rendered v6\"\n    },\n    {\n    \"dataElement\": \"AiPqHCbJQJ1\",\n    \"categoryOptionCombo\": \"DA2N93v7s0O\",\n    \"value\": \"Rendered v2\"\n    },\n    {\n    \"dataElement\": \"AiPqHCbJQJ2\",\n    \"categoryOptionCombo\": \"tSwmrlTW11V\",\n    \"value\": \"Rendered v4\"\n    },\n    {\n    \"dataElement\": \"AiPqHCbJQJ2\",\n    \"categoryOptionCombo\": \"GYRYyntlK7n\",\n    \"value\": \"Rendered v5\"\n    }\n  ]\n}]")
                   template-map1 (assoc template-map1 :template_path "ftl/template1.ftl")
                   template-map2 (assoc template-map2 :template_path "ftl/template2.ftl")]
               (map clojure.data.json/read-str (rt/render-templates [template-map1 template-map2] extra-params "/" query-results))
               =>
               expected)))
