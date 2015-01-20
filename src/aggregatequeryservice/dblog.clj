(ns aggregatequeryservice.dblog
  (:require [aggregatequeryservice.utils :refer :all :as utils]
            [yesql.core :refer [defquery]]
            [cheshire.core :refer :all]
            [aggregatequeryservice.aqstask :refer :all])
  (:import (java.util Date UUID ArrayList))
  (:gen-class
  :methods [[getTaskById [connectionprovider.AQSConnectionProvider Integer] aggregatequeryservice.aqstask.AQSTask]
            [getAllTasks [connectionprovider.AQSConnectionProvider] Object]]))

(defquery insert-task<! "insert_task.sql")
(defquery get-task "get_task.sql")
(defquery update-task! "update_task.sql")
(defquery get-task-by-id* "get_task_by_id.sql")
(defquery get-all-tasks* "get_all_tasks.sql")

(defn insert [aqs-config-path status input-params connection]
  (let [db-spec {:connection connection}
        input-params-json (generate-string input-params)
        query-config-json (utils/read-config aqs-config-path)
        date (new Date)
        uuid (.toString (UUID/randomUUID))]
    (insert-task<! db-spec aqs-config-path status date input-params-json query-config-json uuid)
    (get (first (get-task db-spec uuid)) :aqs_task_id -1)))

(defn update [task-id status results connection]
  (let [db-spec {:connection connection}
        results (generate-string results)]
    (update-task! db-spec status results task-id)))

(defn get-task-by-id [task-id connection]
  (let [db-spec {:connection connection}
        task (first (get-task-by-id* db-spec task-id))]
    (create-record task)))

(defn get-all-tasks [connection]
  (let [db-spec {:connection connection}
        tasks (get-all-tasks* db-spec)]
    (map create-record tasks)))

(defn -getTaskById [this connection-provider task-id]
  (utils/do-with-connection (partial get-task-by-id task-id) connection-provider))

(defn -getAllTasks [this connection-provider]
  (ArrayList. (map convert-to-task (utils/do-with-connection get-all-tasks connection-provider))))
