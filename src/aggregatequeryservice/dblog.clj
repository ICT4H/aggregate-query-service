(ns aggregatequeryservice.dblog
  (:require [aggregatequeryservice.utils :refer :all :as u]
            [yesql.core :refer [defquery]]))

(defquery insert-task<! "insert_task.sql")
(defquery update-task! "update_task.sql")

(defn insert-task [aqs-config-path data-source status]
  (let [db-spec {:datasource data-source}]
    (get (insert-task<! db-spec aqs-config-path (get aqs-config-path :query_json_path) status) :aqs_task_id 0)))

(defn update-task [task_id data-source status]
  (let [db-spec {:datasource data-source}]
    (update-task! db-spec status task_id)))
