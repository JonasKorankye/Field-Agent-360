package express.field.agent.Request.net;

import org.json.JSONObject;

public abstract class BaseCall extends JSONObject {

    private String processId;

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public abstract Object getRequestResult();

    public abstract String getMessage();

}
