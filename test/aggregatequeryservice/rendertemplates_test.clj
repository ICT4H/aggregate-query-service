(ns aggregatequeryservice.rendertemplates-test
  (:import (org.sqlite.javax SQLiteConnectionPoolDataSource))
  (:use [midje.sweet])
  (:require [aggregatequeryservice.rendertemplates :refer :all :as rt]
            [aggregatequeryservice.utils :refer :all :as utils]
            [clojure-test-datasetup.core :as ds]))

(def db-spec {:datasource (doto (new SQLiteConnectionPoolDataSource)
                            (.setUrl "jdbc:sqlite:db/test.db"))})

(def extra-params (hash-map
                    :dataset "Rendered Dataset"
                    :period "20141111"
                    :organization "71345684"
                    ))

(def template-map {
                   :template_path "resources/ftl/template1.ftl"
                   :query_list    '("Query 19" "Query 20")
                   })
(def query-results '({:result         ({:name "Some Name", :id 1} {:id 15, :name "Some First Name"}),
                      :queryGroupname "Query Group 1", :queryName "Query 1", :query "select * from something;"}
                      {:result         ({:name "Some Other Name", :id 2}),
                       :queryGroupname "Query Group 1", :queryName "Query 2", :query "select * from something_else;"}
                      {:result         ({:name "One More Name", :id 6}),
                       :queryGroupname "Query Group 2", :queryName "Query 3", :query "select * from one_more_thing;"}
                      {:result         ({:name "Another Name", :id 4}),
                       :queryGroupname "Query Group 2", :queryName "Query 2", :query "select * from another_thing;"}
                      {:result         ({:name "Some Random Name", :id 99}),
                       :queryGroupname "Query Group Random", :queryName "Query 2", :query "select * from random;"}
                      {:result         ({:v1 "Rendered v1", :v3 "Rendered v3", :v6 "Rendered v6"}),
                       :queryGroupname "Query Group Actual", :queryName "Query 19", :query "select * from some_table;"}
                      {:result         ({:v2 "Rendered v2", :v4 "Rendered v4", :v5 "Rendered v5"}),
                       :queryGroupname "Query Group Actual", :queryName "Query 20", :query "select * from some_other_table;"}))


(facts "Get query result according to name"
       (fact "Return the only query result by query name"
             (rt/get-query-result query-results "Query 3")
             =>
             {:id 6, :name "One More Name"})
       (fact "Throw exception if more than one result found"
             (rt/get-query-result query-results "Query 1")
             =>
             (throws RuntimeException "More/Less than 1 element(s) found"))
       (fact "Take first one if multiple query results with the same name are found"
             (rt/get-query-result query-results "Query 2")
             =>
             {:name "Some Other Name", :id 2})
       (fact "Query Name not found"
             (rt/get-query-result query-results "Query 10")
             =>
             (throws RuntimeException "More/Less than 1 element(s) found")))

(facts "Render templates with query results"
       (fact "Render ftl with query results"
             (rt/render-template extra-params query-results template-map)
             =>
             "{\n  \"dataSet\": \"Rendered Dataset\",\n  \"period\": \"20141111\",\n  \"orgUnit\": \"71345684\",\n  \"dataValues\": [\n    {\n      \"dataElement\": \"AiPqHCbJQJ1\",\n      \"categoryOptionCombo\": \"u2QXNMacZLt\",\n      \"value\": Rendered v1\n    },\n    {\n      \"dataElement\": \"AiPqHCbJQJ1\",\n      \"categoryOptionCombo\": \"DA2N93v7s0O\",\n      \"value\": \"Rendered v2\"\n    },\n    {\n      \"dataElement\": \"AiPqHCbJQJ1\",\n      \"categoryOptionCombo\": \"UBdaznQ8DlT\",\n      \"value\": \"Rendered v3\"\n    },\n    {\n      \"dataElement\": \"AiPqHCbJQJ2\",\n      \"categoryOptionCombo\": \"tSwmrlTW11V\",\n      \"value\": \"Rendered v4\"\n    },\n    {\n      \"dataElement\": \"AiPqHCbJQJ2\",\n      \"categoryOptionCombo\": \"GYRYyntlK7n\",\n      \"value\": \"Rendered v5\"\n    },\n    {\n      \"dataElement\": \"AiPqHCbJQJ2\",\n      \"categoryOptionCombo\": \"KahybAysMCQ\",\n      \"value\": \"Rendered v6\"\n    }\n  ]\n}"))