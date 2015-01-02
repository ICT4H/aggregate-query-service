(ns aggregatequeryservice.dblog
  (:require [aggregatequeryservice.utils :refer :all :as u]
            [yesql.core :refer [defquery]])
  (:import (java.util Date)))

(defquery insert-task<! "insert_task.sql")
(defquery get-task "get_task.sql")
(defquery update-task! "update_task.sql")

(defn insert-task [aqs-config-path data-source status]
  (let [db-spec {:datasource data-source}
        date (new Date)]
    (insert-task<! db-spec aqs-config-path status date)
    (get (first (get-task db-spec aqs-config-path status date)) :aqs_task_id 0)))

(defn update-task [task_id data-source status]
  (let [db-spec {:datasource data-source}]
    (update-task! db-spec status task_id)))
