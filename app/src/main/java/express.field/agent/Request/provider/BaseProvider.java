package express.field.agent.Request.provider;

import android.text.TextUtils;



import java.io.IOException;
import java.security.GeneralSecurityException;

import express.field.agent.Helpers.ResourceProvider;
import express.field.agent.Request.exception.NetworkException;
import express.field.agent.Request.net.BaseCall;
import retrofit2.Response;

public abstract class BaseProvider<T extends BaseCall> {

    public abstract void onResult(T response) throws GeneralSecurityException, IOException;

    public abstract void onException(NetworkException exception);

    public static String getErrorMessage(Response response) {
        switch (response.code()) {
            case 500:
                return ResourceProvider.getString("internal_server_error");
            case 501:
                return ResourceProvider.getString("not_implemented");
            case 502:
                return ResourceProvider.getString("bad_gateway");
            case 503:
                return ResourceProvider.getString("service_unavailable");
            case 504:
                return ResourceProvider.getString("gateway_timeout");
            case 505:
                return ResourceProvider.getString("http_version_not_supported");
            case 511:
                return ResourceProvider.getString("network_authentication_required");
            default:
                try {
                    String msg = response.errorBody().string();
                    if (TextUtils.isEmpty(msg)) {
                        return ResourceProvider.getString("unknown_network_error_message");
                    } else {
                        return msg;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return ResourceProvider.getString("unknown_network_error_message");
                }
        }
    }

}
