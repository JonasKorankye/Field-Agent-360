package express.field.agent.Model;

public class ProfilePhone {
    private int phoneId;
    private String actorId;
    private String frontEndRecordId;
    private String phoneTypeId;
    private String phoneNumber;
    private String statusId;
    private String mnoId;
    private boolean isPrimary;

    public int getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(int phoneId) {
        this.phoneId = phoneId;
    }

    public String getActorId() {
        return actorId;
    }

    public void setActorId(String actorId) {
        this.actorId = actorId;
    }

    public String getFrontEndRecordId() {
        return frontEndRecordId;
    }

    public void setFrontEndRecordId(String frontEndRecordId) {
        this.frontEndRecordId = frontEndRecordId;
    }

    public String getPhoneTypeId() {
        return phoneTypeId;
    }

    public void setPhoneTypeId(String phoneTypeId) {
        this.phoneTypeId = phoneTypeId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public String getMnoId() {
        return mnoId;
    }

    public void setMnoId(String mnoId) {
        this.mnoId = mnoId;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }
}
