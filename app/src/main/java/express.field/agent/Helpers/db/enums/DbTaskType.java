package express.field.agent.Helpers.db.enums;

public enum DbTaskType {
    NEW, ACTIVE, BLOCKED, PENDING, APPROVED, REJECTED, NOT_RECOGNIZED;

    @Override
    public String toString() {
        return name();
    }

    public boolean is(String status) {
        return name().equals(status);
    }

    public boolean is(Object modelname) {
        if (modelname instanceof String) {
            return is((String) modelname);
        } else {
            return modelname == this;
        }
    }

    public static DbTaskType valueOf(Object typeName) {
        if (typeName instanceof String) {
            String type = (String) typeName;
            for (DbTaskType value : values()) {
                if (value.toString().equalsIgnoreCase(type)) {
                    return value;
                }
            }
        } else if (typeName instanceof DbTaskType) {
            return (DbTaskType) typeName;
        }

        return NOT_RECOGNIZED;
    }

    public static boolean isValidStatus(final String modelName) {
        return valueOf(modelName) != NOT_RECOGNIZED;
    }
}

