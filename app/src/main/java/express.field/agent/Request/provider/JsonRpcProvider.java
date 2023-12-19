package express.field.agent.Request.provider;

import androidx.annotation.NonNull;


import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

import express.field.agent.Helpers.ResourceProvider;
import express.field.agent.Request.RetrofitManager;
import express.field.agent.Request.exception.NetworkException;
import express.field.agent.Request.net.JsonRpcCall;
import express.field.agent.Utils.Constants;
import express.field.agent.Utils.ObjectUtils;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

/**
 * Creates JSON RPC requests and parses the responses
 */
public abstract class JsonRpcProvider extends BaseProvider<JsonRpcCall> implements Callback<JsonRpcCall> {

    private static int idCounter = 0;
    private static final String BE_KEY_CIPHERTEXT = "ciphertext";

    public static JsonRpcCall onResponseSync(Call<JsonRpcCall> call, Response<JsonRpcCall> response) throws NetworkException {
        if (response.body() != null) {
            JsonRpcCall body = response.body();
            if (body.getError() != null) {
                NetworkException e = new NetworkException(body.getError().getMessage());
//                FirebaseAnalyticsService.getInstance().logNetworkEvent("error", e.getMessage());
                throw e;
            } else {
                return response.body();
            }
        } else {
            NetworkException e = new NetworkException(parseError(response).getMessage());
//            FirebaseAnalyticsService.getInstance().logNetworkEvent("error", e.getMessage());
            throw e;
        }
    }

    @Override
    public void onResponse(Call<JsonRpcCall> call, Response<JsonRpcCall> response) {
        if (response.body() != null) {
            JsonRpcCall body = response.body();
            if (body.getError() != null) {
                Object printMsg = getTranslatedErrorMessage(body);

                if (printMsg == null) {
                    printMsg = body.getError().getMessage();
                }

                NetworkException exception = new NetworkException(String.valueOf(printMsg));
//                FirebaseAnalyticsService.getInstance().logNetworkEvent(Constants.ERROR_RESPONSE, exception.getMessage());

                if (body.getError().getType() != null) {
                    exception.setType(body.getError().getType());
                }

                onException(exception);
            } else {
                try {

                    onResult(response.body());

                } catch (GeneralSecurityException | IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JsonRpcCall jsonRpcCall = parseError(response);

            Object printMsg = getTranslatedErrorMessage(jsonRpcCall);

            if (printMsg == null) {
                printMsg = jsonRpcCall.getMessage();
            }

            NetworkException exception = new NetworkException(String.valueOf(printMsg));
            exception.setResponse(jsonRpcCall);

//            FirebaseAnalyticsService.getInstance().logNetworkEvent(Constants.ERROR_RESPONSE, String.valueOf(printMsg));
            if (jsonRpcCall.getError() != null && jsonRpcCall.getError().getType() != null) {
                exception.setType(jsonRpcCall.getError().getType());
            }

            onException(exception);
        }
    }

    @Override
    public void onFailure(Call<JsonRpcCall> call, @NonNull Throwable t) {
        if (t instanceof UnknownHostException) {
            onException(new NetworkException(ResourceProvider.getString("unknown_network_error_message")));
        } else if (t instanceof IOException) {
            onException(new NetworkException(ResourceProvider.getString("data_connection_error")));
        } else if (containsIpAddress(t.getMessage())) {
            onException(new NetworkException(ResourceProvider.getString("technical_connection_error")));
        }else {
            onException(new NetworkException(t.getMessage()));
        }

    }

    public boolean containsIpAddress(String s) {
        java.util.regex.Matcher m = java.util.regex.Pattern.compile(
            "(?<!\\d|\\d\\.)" +
                "(?:[01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "(?:[01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "(?:[01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "(?:[01]?\\d\\d?|2[0-4]\\d|25[0-5])" +
                "(?!\\d|\\.\\d)").matcher(s);
        return m.find();
    }


//    public static RequestBody createJsonRpcRequest(String method, Object params) {
//
//        //keep method
//
//        GlobalPref.getInstance().put(Constants.METHOD, method);
//        String mleMap = null;
//        try {
//            mleMap = Crypto.preparePayload(ObjectUtils.objectToJsonString(params));
//        } catch (CryptoException e) {
//            e.printStackTrace();
//        }
//
//        JsonRpcCall request = getJsonRpc(method, mleMap);
//        return RequestBody.create(Constants.APPLICATION_JSON, ObjectUtils.objectToJson(request));
//    }
//
//    public static RequestBody createRequestForEncryption(String method, Object params) throws CryptoException {
//        //keep method
//
//        GlobalPref.getInstance().put(Constants.METHOD, method);
//        String mleMap = Crypto.preparePayload(ObjectUtils.objectToJsonString(params));
//
//        JsonRpcCall request = getJsonRpc(method, mleMap);
//
//        return RequestBody.create(Constants.APPLICATION_JSON, ObjectUtils.objectToJson(request));
//    }

    public static RequestBody createCryptoJsonRpcRequest(String method, Object params) {
        JsonRpcCall request = getJsonRpc(method, params);

        return RequestBody.create(Constants.APPLICATION_JSON, ObjectUtils.objectToJson(request));
    }

    public static ResponseBody createCryptoJsonRpcResponse(String method, Object params) {
        JsonRpcCall response = getResponseJsonRpc(method, params);

        return ResponseBody.create(Constants.APPLICATION_JSON, ObjectUtils.objectToJson(response));
    }

    private static JsonRpcCall parseError(Response response) {
        JsonRpcCall ex = new JsonRpcCall();
        if (response.errorBody() != null) {
            try {
                Converter<ResponseBody, JsonRpcCall> converter = RetrofitManager.getBaseUrlRetrofitInstance(Constants.BASE_URL)
                    .responseBodyConverter(JsonRpcCall.class, new Annotation[0]);

                byte[] bytes = response.errorBody().bytes();
                String str = new String(bytes, StandardCharsets.UTF_8); // for UTF-8 encoding

//                if (str.contains(BE_KEY_CIPHERTEXT)) {
//                    UtResponsePrepared decrypted = null;
//                    try {
//                        decrypted = Crypto.parseResponse(bytes);
//                    } catch (HttpCallException e) {
//                        e.printStackTrace();
//                    }
//
//                    ex.setMessage(decrypted.getError().getMessage());
//
//                } else {
                    ex = converter.convert(response.errorBody());
//                }

            } catch (IOException e) {
                ex.setMessage(getErrorMessage(response));
            }
        } else {
            ex.setMessage(getErrorMessage(response));
        }

        return ex;
    }


    public static JsonRpcCall getJsonRpc(String method, Object params) {
        JsonRpcCall request = new JsonRpcCall();
        request.setId(getRequestId());
        request.setJsonrpc(Constants.JSON_RPC);
        request.setMethod(method);
        request.setParams(params);

        return request;
    }

    public static JsonRpcCall getResponseJsonRpc(String method, Object params) {
        JsonRpcCall request = new JsonRpcCall();
        request.setId(getRequestId());
        request.setJsonrpc(Constants.JSON_RPC);
        request.setMethod(method);
        request.setResult(params);

        return request;
    }


    private static synchronized String getRequestId() {
        return ++idCounter + "";
    }

    private Object getTranslatedErrorMessage(JsonRpcCall jsonRpcCall) {
        Object printMsg = null;

        String languageCode = "en_US";

        if (languageCode != null
            && !Constants.DEFAULT_LOCALE.getLanguage().equals(languageCode)) {
            if (jsonRpcCall.getError() != null && jsonRpcCall.getError().getPrint() != null) {
                String print = jsonRpcCall.getError().getPrint();

            }
        }

        return printMsg;
    }
}
