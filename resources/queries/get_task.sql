-- Get aqs task
select aqs_task_id from aqs_task where aqs_config_path= ? and task_status= ? and date_created= ?;