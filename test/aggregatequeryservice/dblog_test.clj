(ns aggregatequeryservice.dblog-test
  (:import (org.sqlite.javax SQLiteConnectionPoolDataSource))
  (:use [midje.sweet])
  (:require [clojure-test-datasetup.core :as ds]
            [aggregatequeryservice.dblog :as dblog]
            [clojure.java.jdbc :as jdbc]))

(def datasource (doto (new SQLiteConnectionPoolDataSource)
                  (.setUrl "jdbc:sqlite:db/dblogtest.db")))
(def db-spec {:datasource datasource})

(facts "Insert and Update tasks in db"
       (with-state-changes [(before :facts (dorun (ds/setup-dataset "resources/dblog-dataset.json" db-spec)))
                            (after :facts (ds/tear-down-dataset "resources/dblog-dataset.json" db-spec))]
                           (fact "Insert the task"
                                 (dblog/insert-task "test-aqs-config-path" datasource "IN PROGRESS")
                                 (let [result (jdbc/query db-spec ["select * from aqs_task where aqs_config_path=?;" "test-aqs-config-path"])
                                       [{aqs-config-path :aqs_config_path task-status :task_status}] result]
                                   aqs-config-path
                                   =>
                                   "test-aqs-config-path"
                                   task-status
                                   =>
                                   "IN PROGRESS"))))





