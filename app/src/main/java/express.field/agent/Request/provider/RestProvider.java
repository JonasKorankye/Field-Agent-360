package express.field.agent.Request.provider;

import android.text.TextUtils;
import java.io.IOException;
import java.security.GeneralSecurityException;

import express.field.agent.Helpers.ResourceProvider;
import express.field.agent.Request.exception.NetworkException;
import express.field.agent.Request.net.RestCall;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Provides RestCall results in app-workable format
 */
public abstract class RestProvider extends BaseProvider<RestCall> implements Callback<RestCall> {

    public static RestCall onResponseSync(Call<RestCall> call, Response<RestCall> response) throws NetworkException {
        RestCall result = new RestCall();
        result.setCode(response.code());
        result.setMethod(response.raw().request().method());
        result.setUrl(response.raw().request().url().toString());
        if (result.getCode() >= 200 && result.getCode() <= 206){
            if (response.body() != null) {
                result.setResponseBody(response.body());
            }
            return result;
        } else if (result.getCode() >= 400 && result.getCode() < 500) {
            try {
                NetworkException t =  new NetworkException(response.errorBody().string());
//                FirebaseAnalyticsService.getInstance().logNetworkEvent("error", t.getMessage());
                throw t;
            } catch (IOException e) {
                e.printStackTrace();
                throw new NetworkException(e.getMessage());
            }
        } else if (result.getCode() >= 500 && result.getCode() < 512) {
            String message = getErrorMessage(response);
            String m = TextUtils.isEmpty(result.getMessage()) ? "" : result.getMessage();
//            FirebaseAnalyticsService.getInstance().logNetworkEvent("error", m);
            throw new NetworkException(ResourceProvider.getString("server_returned_an_error") + m + " " + message);
        } else {
            throw new NetworkException(response.raw().message());
        }
    }

    @Override
    public void onResponse(Call<RestCall> call, Response<RestCall> response) {
        RestCall result = new RestCall();
        result.setCode(response.code());
        result.setMethod(response.raw().request().method());
        result.setUrl(response.raw().request().url().toString());
        if (result.getCode() >= 200 && result.getCode() <= 206){
            if (response.body() != null) {
                result.setResponseBody(response.body());
            }
            try {
                onResult(result);

            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
            }
        } else if (result.getCode() >= 400 && result.getCode() < 500) {
            try {
                Throwable t = new Throwable(response.errorBody().string());
//                FirebaseAnalyticsService.getInstance().logNetworkEvent("error", t.getMessage());
                onFailure(call, t);
            } catch (IOException e) {
                onFailure(call, new Throwable(e));
                e.printStackTrace();
            }
        } else if (result.getCode() >= 500 && result.getCode() < 512 ) {
            String message = getErrorMessage(response);
            String m = TextUtils.isEmpty(result.getMessage()) ? "" : result.getMessage();
//            FirebaseAnalyticsService.getInstance().logNetworkEvent("error", m);
            onFailure(call, new Throwable(ResourceProvider.getString("server_returned_an_error") + m + " " + message));
        } else {
            result.setMessage(response.raw().message());
            onFailure(call, new Throwable("Something went very wrong"));
        }
    }

    @Override
    public void onFailure(Call<RestCall> call, Throwable t) {
        onException(new NetworkException(t.getMessage()));
    }

}
