package express.field.agent.Request.net;


public class UtResponsePrepared<T> extends JsonObject {
    private static final long serialVersionUID = 1L;

    private String jsonrpc;
    private String id;

    public UtResponsePrepared() {
    }

    public UtResponsePrepared(UtError error) {
        this.error = error;
    }

    public UtResponsePrepared(T result) {
        this.result = result;
    }

    private UtError error;
    private T result;

    public UtError getError() {
        return error;
    }

    public void setError(UtError error) {
        this.error = error;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
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
}
