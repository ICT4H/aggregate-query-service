(ns aggregatequeryservice.dblog
  (:require [aggregatequeryservice.utils :refer :all :as utils]
            [yesql.core :refer [defquery]]
            [cheshire.core :refer :all]
            )
  (:import (java.util Date UUID ArrayList)
           (aggregatequeryservice.aqstask AQSTask)
           (connectionprovider AQSConnectionProvider))
  (:gen-class
  :methods [[getTaskById [connectionprovider.AQSConnectionProvider Integer] Object]
            [getAllTasks [connectionprovider.AQSConnectionProvider] java.util.ArrayList]
            [getAllTasksInProgress [connectionprovider.AQSConnectionProvider] java.util.ArrayList]]))

(defquery insert-task<! "insert_task.sql")
(defquery get-task "get_task.sql")
(defquery update-task! "update_task.sql")
(defquery get-task-by-id* "get_task_by_id.sql")
(defquery get-all-tasks* "get_all_tasks.sql")
(defquery get-all-in-progress-tasks* "get_all_in_progress_tasks.sql")

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
    task))

(defn get-all-tasks [connection]
  (let [db-spec {:connection connection}
        tasks (get-all-tasks* db-spec)]
    tasks))

(defn get-in-progress-tasks [connection]
  (let [db-spec {:connection connection}
        tasks (get-all-in-progress-tasks* db-spec)]
    tasks))

(defn strip-results [hashmap]
  (dissoc hashmap :results))

(defn create-aqs-task [hashmap]
  (let [{taskId :aqs_task_id config_path :aqs_config_path status :task_status date_created :date_created results :results input_parameters :input_parameters query_config :query_config uuid :uuid} hashmap]
    (new AQSTask taskId config_path status date_created results input_parameters query_config uuid)))

(def create-aqs-task-without-result (comp create-aqs-task strip-results))

(defn -getTaskById [this connection-provider task-id]
  (create-aqs-task (utils/do-with-connection (partial get-task-by-id task-id) connection-provider)))

(defn ^java.util.ArrayList -getAllTasks [this connection-provider]
  (ArrayList. (map create-aqs-task-without-result (utils/do-with-connection get-all-tasks connection-provider))))

(defn ^java.util.ArrayList -getAllTasksInProgress [this connection-provider]
  (ArrayList. (map create-aqs-task-without-result (utils/do-with-connection get-in-progress-tasks connection-provider))))