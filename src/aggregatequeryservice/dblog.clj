(ns aggregatequeryservice.dblog
  (:require [aggregatequeryservice.utils :refer :all :as u]
            [yesql.core :refer [defquery]])
  (:use clojure.data.json)
  (:import (java.util Date)))

(defquery insert-task<! "insert_task.sql")
(defquery get-task "get_task.sql")
(defquery update-task! "update_task.sql")

(defn insert-task [data-source aqs-config-path status input-params]
  (let [db-spec {:datasource data-source}
        input-params-json (write-str input-params)
        query-config-json (u/read-config aqs-config-path)
        date (new Date)]
    (insert-task<! db-spec aqs-config-path status date input-params-json query-config-json)
    (get (first (get-task db-spec aqs-config-path status date)) :aqs_task_id 0)))

(defn update-task [data-source task-id status results]
  (let [db-spec {:datasource data-source}
        results (write-str results)]
    (update-task! db-spec status results task-id)))