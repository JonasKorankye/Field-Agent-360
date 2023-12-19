package express.field.agent.Helpers.db.enums;

public enum DbObjectSyncStatus {
    PENDING_DOWNLOAD, DOWNLOADED, UPLOADED, OVERRIDE, FAILED, DRAFT, CONFIRMED, SCHEDULED, SYNCING,
    NOT_RECOGNIZED;

    @Override
    public String toString() {
        return name();
    }

    public boolean is(String status) {
        return name().equals(status);
    }

    public boolean is(Object modelname) {
        if (modelname instanceof String) {
            return is( (String) modelname);
        } else {
            return modelname == this;
        }
    }

    public static DbObjectSyncStatus valueOf(Object statusName) {
        if (statusName instanceof String) {
            String status = (String) statusName;
            for (DbObjectSyncStatus value : values()) {
                if (value.toString().equalsIgnoreCase(status)) {
                    return value;
                }
            }
        } else if (statusName instanceof DbObjectSyncStatus) {
            return (DbObjectSyncStatus) statusName;
        }

        return NOT_RECOGNIZED;
    }

    public static boolean isValidStatus(final String modelName) {
        return valueOf(modelName) != NOT_RECOGNIZED;
    }

}
