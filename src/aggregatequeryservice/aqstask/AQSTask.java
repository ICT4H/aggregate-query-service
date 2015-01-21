package aggregatequeryservice.aqstask;

import java.sql.Timestamp;

public class AQSTask {
    private Long taskId;
    private String aqsConfigPath;
    private String taskStatus;
    private Timestamp dateCreated;
    private String results;
    private String inputParameters;
    private String queryConfig;
    private String uuid;

    public AQSTask() {
    }

    public AQSTask(Long taskId, String aqsConfigPath, String taskStatus, Timestamp dateCreated, String results, String inputParameters, String queryConfig, String uuid) {
        this.taskId = taskId;
        this.aqsConfigPath = aqsConfigPath;
        this.taskStatus = taskStatus;
        this.dateCreated = dateCreated;
        this.results = results;
        this.inputParameters = inputParameters;
        this.queryConfig = queryConfig;
        this.uuid = uuid;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getAqsConfigPath() {
        return aqsConfigPath;
    }

    public void setAqsConfigPath(String aqsConfigPath) {
        this.aqsConfigPath = aqsConfigPath;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }

    public String getInputParameters() {
        return inputParameters;
    }

    public void setInputParameters(String inputParameters) {
        this.inputParameters = inputParameters;
    }

    public String getQueryConfig() {
        return queryConfig;
    }

    public void setQueryConfig(String queryConfig) {
        this.queryConfig = queryConfig;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
