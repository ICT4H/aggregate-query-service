(ns aggregatequeryservice.dblog
  (:require [aggregatequeryservice.utils :refer :all :as utils]
            [yesql.core :refer [defquery]]
            [cheshire.core :refer :all])
  (:import (java.util Date))
  (:gen-class
  :methods [[getTaskById [javax.sql.DataSource Integer] Object]
            [getAllTasks [javax.sql.DataSource] Object]]))

(defquery insert-task<! "insert_task.sql")
(defquery get-task "get_task.sql")
(defquery update-task! "update_task.sql")
(defquery get-task-by-id* "get_task_by_id.sql")
(defquery get-all-tasks* "get_all_tasks.sql")

(defn insert [aqs-config-path status input-params connection]
  (let [db-spec {:connection connection}
        input-params-json (generate-string input-params)
        query-config-json (utils/read-config aqs-config-path)
        date (new Date)]
    (insert-task<! db-spec aqs-config-path status date input-params-json query-config-json)
    (get (first (get-task db-spec aqs-config-path status date)) :aqs_task_id 0)))

(defn update [task-id status results connection]
  (let [db-spec {:connection connection}
        results (generate-string results)]
    (update-task! db-spec status results task-id)))

(defn get-task-by-id [task-id connection]
  (let [db-spec {:connection connection}]
    (first (get-task-by-id* db-spec task-id))))

(defn get-all-tasks [connection]
  (let [db-spec {:connection connection}]
    (get-all-tasks* db-spec)))

(defn -getTaskById [this connection-provider task-id]
  (utils/do-with-connection (partial get-task-by-id task-id) connection-provider))

(defn -getAllTasks [this connection-provider]
  (generate-string (utils/do-with-connection get-all-tasks connection-provider)))