package express.field.agent.Request.exception;


import express.field.agent.Request.net.BaseCall;
import express.field.agent.Request.net.JsonRpcCall;
import express.field.agent.Request.net.RestCall;

public class NetworkException extends Throwable {

    private String id;
    private String type;
    private BaseCall response;

    public NetworkException(String message) {
        super(message);
    }

    public NetworkException(String message, String id) {
        super(message);
        this.id = id;
    }

    public NetworkException(String message, RestCall restCall) {
        super(message);
        this.restCall = restCall;
    }

    public NetworkException(String message, JsonRpcCall jsonRpcCall) {
        super(message);
        this.jsonRpcCall = jsonRpcCall;
    }

    private RestCall restCall;
    private JsonRpcCall jsonRpcCall;

    public RestCall getRestCall() {
        return restCall;
    }

    public void setRestCall(RestCall restCall) {
        this.restCall = restCall;
    }

    public JsonRpcCall getJsonRpcCall() {
        return jsonRpcCall;
    }

    public void setJsonRpcCall(JsonRpcCall jsonRpcCall) {
        this.jsonRpcCall = jsonRpcCall;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BaseCall getResponse() {
        return response;
    }

    public void setResponse(BaseCall response) {
        this.response = response;
    }
}
