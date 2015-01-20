(ns aggregatequeryservice.aqstask)
(defrecord AQSTask [taskId aqsConfigPath taskStatus dateCreated results inputParameters queryConfig uuid])
(defn create-record [task]
  (let [{task-id :aqs_task_id config-path :aqs_config_path status :task_status date-created :date_created results :results input-params :input_parameters query-config :query_config uuid :uuid} task]
    (->AQSTask task-id config-path status date-created results input-params query-config uuid)))