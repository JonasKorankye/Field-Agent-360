package express.field.agent.Request.net;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UtResponseSecured extends JsonObject {

    private static final long serialVersionUID = 1L;

    @JsonProperty("jsonrpc")
    private String jsonRpc;

    private String id;
    private UtResponseJWE error;
    private UtResponseJWE result;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UtResponseJWE getError() {
        return error;
    }

    public void setError(UtResponseJWE error) {
        this.error = error;
    }

    public UtResponseJWE getResult() {
        return result;
    }

    public void setResult(UtResponseJWE result) {
        this.result = result;
    }

    public String getJsonRpc() {
        return jsonRpc;
    }

    public void setJsonRpc(String jsonRpc) {
        this.jsonRpc = jsonRpc;
    }

    public boolean hasErrors() {
        return error != null;
    }
}
