package express.field.agent.Request.net;


import java.util.Map;

public class JsonRpcCall extends BaseCall {

    private String jsonrpc;
    private String id;
    private String method;
    private Object params;
    private Object result;
    private int statusCode;
    private JsonRpcCallError error;
    private String message;
    private String type;
    private String level;


    public JsonRpcCall(Map<String, Object> result) {
        this.result = result;
    }

    public JsonRpcCall() {

    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object getParams() {
        return params;
    }

    public void setParams(Object params) {
        this.params = params;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JsonRpcCall)) return false;

        JsonRpcCall jsonRpcCall = (JsonRpcCall) o;


        if (id != null ? !id.equals(jsonRpcCall.id) : jsonRpcCall.id != null) return false;
        if (jsonrpc != null ? !jsonrpc.equals(jsonRpcCall.jsonrpc) : jsonRpcCall.jsonrpc != null)
            return false;
        if (method != null ? !method.equals(jsonRpcCall.method) : jsonRpcCall.method != null)
            return false;
        if (params != null ? !params.equals(jsonRpcCall.params) : jsonRpcCall.params != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = jsonrpc != null ? jsonrpc.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (method != null ? method.hashCode() : 0);
        result = 31 * result + (params != null ? params.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("{jsonrpc:'%s', id:'%s', method:'%s'}", jsonrpc, id, method, params != null ? params.toString() : "null");
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public JsonRpcCallError getError() {
        return error;
    }

    public void setError(JsonRpcCallError error) {
        this.error = error;
    }

    public boolean hasErrors() {
        return error != null;
    }

    public String getMessage() {
        return message != null ? message : error != null ? error.getMessage() : null;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public Object getRequestResult() {
        return getResult();
    }

}
