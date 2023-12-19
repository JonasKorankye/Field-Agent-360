package express.field.agent.Request.net;

import java.util.Map;

public class JsonRpcCallError {

    private int code;
    private String message;
    private String errorPrint;
    private String type;
    private String print;
    private Map<String, Object> params;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorPrint() {
        return errorPrint;
    }

    public void setErrorPrint(String errorPrint) {
        this.errorPrint = errorPrint;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPrint() {
        return print;
    }

    public void setPrint(String print) {
        this.print = print;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
