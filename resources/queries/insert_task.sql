-- Inserts an AQS Task in the aqs_task table
INSERT INTO aqs_task (aqs_config_path, task_status, date_created, input_parameters, query_config) VALUES (?, ?, ?, ?, ?);