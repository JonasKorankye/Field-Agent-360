package express.field.agent.Model.db;

public class ModelHistoryEntry {

    public ModelHistoryEntry(){
    }

    public ModelHistoryEntry(String syncStatus, String timeStamp, String message) {
        this.syncStatus = syncStatus;
        this.timeStamp = timeStamp;
        this.message = message;
    }

    public ModelHistoryEntry(String syncStatus, String timeStamp) {
        this.syncStatus = syncStatus;
        this.timeStamp = timeStamp;
    }

    private String syncStatus;
    private String timeStamp;
    private String message;

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
