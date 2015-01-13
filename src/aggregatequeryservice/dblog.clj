(ns aggregatequeryservice.dblog
  (:require [aggregatequeryservice.utils :refer :all :as u]
            [yesql.core :refer [defquery]])
  (:use clojure.data.json)
  (:import (java.util Date))
  (:gen-class
    :methods [[getTaskById [javax.sql.DataSource Integer] Object]
              [getAllTasks [javax.sql.DataSource] Object]]
  ))

(defquery insert-task<! "insert_task.sql")
(defquery get-task "get_task.sql")
(defquery update-task! "update_task.sql")
(defquery get-task-by-id* "get_task_by_id.sql")
(defquery get-all-tasks* "get_all_tasks.sql")

(defn insert [data-source aqs-config-path status input-params]
  (let [db-spec {:datasource data-source}
        input-params-json (write-str input-params)
        query-config-json (u/read-config aqs-config-path)
        date (new Date)]
    (insert-task<! db-spec aqs-config-path status date input-params-json query-config-json)
    (get (first (get-task db-spec aqs-config-path status date)) :aqs_task_id 0)))

(defn update [data-source task-id status results]
  (let [db-spec {:datasource data-source}
        results (write-str results)]
    (update-task! db-spec status results task-id)))

(defn get-task-by-id [datasource task-id]
  (let [db-spec {:datasource datasource}]
    (first (get-task-by-id* db-spec task-id))))

(defn get-all-tasks [datasource]
  (let [db-spec {:datasource datasource}]
    (get-all-tasks* db-spec)))

(defn -getTaskById [datasource task-id]
  (get-task-by-id datasource task-id))

(defn -getAllTasks [this datasource]
  ; TODO - Mujir - this isnt robust as db column value knowledge is needed.
  (defn custom-date-writer [key value]
    (if (= key :date_created)
      (str (java.sql.Date. (.getTime value)))
      value))

    (clojure.data.json/write-str (get-all-tasks datasource)
                                 :value-fn custom-date-writer
                                 :key-fn name
                                 ))

