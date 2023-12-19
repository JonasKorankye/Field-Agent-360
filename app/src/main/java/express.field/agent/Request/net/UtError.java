package express.field.agent.Request.net;

import androidx.annotation.NonNull;


import org.jetbrains.annotations.NotNull;

public class UtError extends JsonObject {
    private static final long serialVersionUID = 1L;

    private String method;
    private String message;
    private String type;
    private Cause cause;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Cause getCause() {
        return cause;
    }

    public void setCause(Cause cause) {
        this.cause = cause;
    }

    public String getCode() {
        return getCause().getCode();
    }

    @NotNull
    @Override
    public String toString() {
        return String.format("{method:'%s' , message:'%s', type: '%s', cause: '%s'}", method, message, type, cause);
    }

    public static class Cause {
        private long number;
        private String code;
        private String name;
        private String serverName;
        private String procName;
        private int state;
        private int lineNumber;

        private int classNumber;

        public long getNumber() {
            return number;
        }

        public void setNumber(long number) {
            this.number = number;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getServerName() {
            return serverName;
        }

        public void setServerName(String serverName) {
            this.serverName = serverName;
        }

        public String getProcName() {
            return procName;
        }

        public void setProcName(String procName) {
            this.procName = procName;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public void setLineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
        }

        public int getClassNumber() {
            return classNumber;
        }

        public void setClassNumber(int classNumber) {
            this.classNumber = classNumber;
        }

        @NonNull
        @Override
        public String toString() {
            return String.format("{number:'%s' , code:'%s', name:'%s', serverName:'%s', procName:'%s', state:'%s', lineNumber:'%s', class:'%s'}", number, code, name, serverName, procName, state, lineNumber, classNumber);
        }
    }
}
