(ns aggregatequeryservice.dblog-test
  (:import (org.sqlite.javax SQLiteConnectionPoolDataSource))
  (:use [midje.sweet])
  (:require [clojure-test-datasetup.core :as ds]
            [aggregatequeryservice.dblog :as dblog]
            [clojure.java.jdbc :as jdbc]))

(def datasource (doto (new SQLiteConnectionPoolDataSource)
                  (.setUrl "jdbc:sqlite:db/dblogtest.db")))
(def db-spec {:datasource datasource})

(def ^:private results-json
  "{\"dataSet\":\"Rendered Dataset\",\"orgUnit\":\"71345684\",\"period\":\"20141111\",\"dataValues\":[{\"dataElement\":\"AiPqHCbJQJ1\",\"categoryOptionCombo\":\"u2QXNMacZLt\",\"value\":\"Rendered v1\"},{\"dataElement\":\"AiPqHCbJQJ1\",\"categoryOptionCombo\":\"UBdaznQ8DlT\",\"value\":\"Rendered v3\"},{\"dataElement\":\"AiPqHCbJQJ2\",\"categoryOptionCombo\":\"KahybAysMCQ\",\"value\":\"Rendered v6\"}]}")

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

(def ^:private task-id 1)

(facts "Insert and Update tasks in db"
       (with-state-changes [(before :facts (dorun (ds/setup-dataset "resources/dblog-dataset.json" db-spec)))
                            (after :facts (ds/tear-down-dataset "resources/dblog-dataset.json" db-spec))]
                           (fact "Insert the task"
                                 (let [query-params (hash-map :renderThis "rdThis" :renderThat "rdThat" :replaceThis "rpThis" :replaceThat "rpThat")
                                       task-id (dblog/insert-task datasource "sample_config.json" "IN PROGRESS" query-params)
                                       result (jdbc/query db-spec ["select * from aqs_task where aqs_config_path=?;" "sample_config.json"])
                                       [{aqs-config-path :aqs_config_path task-status :task_status actual-task-id :aqs_task_id query-config :query_config input-params :input_parameters}] result]
                                   (clojure.data.json/read-str input-params :key-fn keyword)
                                   =>
                                   query-params

                                   (clojure.data.json/read-str query-config :key-fn keyword)
                                   =>
                                   (aggregatequeryservice.utils/read-config-to-map "sample_config.json")

                                   task-id
                                   =>
                                   actual-task-id

                                   aqs-config-path
                                   =>
                                   "sample_config.json"

                                   task-status
                                   =>
                                   "IN PROGRESS"))
                           (fact "Update Task"
                                 (let [update (dblog/update-task datasource 1 "DONE" query-results)
                                       updated-row (jdbc/query db-spec ["select * from aqs_task where aqs_task_id=?;" 1])
                                       [{aqs-config-path :aqs_config_path task-status :task_status actual-task-id :aqs_task_id query-config :query_config input-params :input_parameters date-created :date_created results :results}] updated-row]
                                   "config-path"
                                   =>
                                   aqs-config-path

                                   "DONE"
                                   =>
                                   task-status

                                   "somedate"
                                   =>
                                   date-created

                                   query-results
                                   =>
                                   (clojure.data.json/read-str results :key-fn keyword)))
                           (fact "Given a task id return the task"
                                 (let [result (dblog/get-task-by-id datasource task-id)
                                       {aqs-config-path :aqs_config_path task-status :task_status actual-task-id :aqs_task_id query-config :query_config input-params :input_parameters date-created :date_created results :results} result]
                                   "config-path"
                                   =>
                                   aqs-config-path

                                   "in progress"
                                   =>
                                   task-status

                                   "somedate"
                                   =>
                                   date-created

                                   (clojure.data.json/read-str results-json :key-fn keyword)
                                   =>
                                   (clojure.data.json/read-str results :key-fn keyword)))
                           (fact "Given a non existent task id return empty list"
                                 (let [result (dblog/get-task-by-id datasource 0)]
                                   true
                                   =>
                                   (nil? result)))))