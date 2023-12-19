package express.field.agent.security;


import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import express.field.agent.Pref.GlobalPref;
import express.field.agent.R;
import express.field.agent.Request.exception.HttpCallException;
import express.field.agent.Request.net.UtResponsePrepared;
import express.field.agent.Request.provider.JsonRpcProvider;
import express.field.agent.Utils.Constants;
import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * Retrofit Interceptor to intercept and decrypt response from the server
 */
public class DecryptionInterceptor implements Interceptor {
    private ResponseBody cryptoJsonRpcResponse;

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {

        Response response = chain.proceed(chain.request());
        if (response.isSuccessful()) {
            Response.Builder newResponse = response.newBuilder();
            String contentType = response.header("Content-Type");
            if (TextUtils.isEmpty(contentType)) contentType = "application/json";
            byte[] responseBytes = response.body().bytes();

            String str = new String(responseBytes, StandardCharsets.UTF_8); // for UTF-8 encoding

            Map results = null;
            Object resultList = null;
            String method = GlobalPref.getInstance().getString(Constants.METHOD, "");


            try {
                UtResponsePrepared decrypted = Crypto.parseResponse(responseBytes);

                if (decrypted.getResult() instanceof Map) {
                    results = (HashMap) decrypted.getResult();


                    if (results != null && !results.isEmpty()) {
                        cryptoJsonRpcResponse = JsonRpcProvider.
                                createCryptoJsonRpcResponse(method, results);
                    } else {
                        if (decrypted.getError() != null) {
                            throw new HttpCallException(decrypted.getError().getMessage());
                        }
                        HashMap<String, Object> emptyResult = new HashMap<>();
                        emptyResult.put("message", "No Response");
                        cryptoJsonRpcResponse = JsonRpcProvider.
                                createCryptoJsonRpcResponse(method, emptyResult);
                        throw new HttpCallException("Can not continue recently!");
                    }
                } else {
                    resultList = decrypted.getResult();
                    System.out.println("resultList" + resultList);
                    cryptoJsonRpcResponse = JsonRpcProvider.
                            createCryptoJsonRpcResponse(method, resultList);

                    if (resultList != null) {
                        cryptoJsonRpcResponse = JsonRpcProvider.
                                createCryptoJsonRpcResponse(method, resultList);


                    } else {
                        if (decrypted.getError() != null) {
                            throw new HttpCallException(decrypted.getError().getMessage());
                        }


                        HashMap<String, Object> emptyResult = new HashMap<>();
                        emptyResult.put("message", "No Response");


                        cryptoJsonRpcResponse = JsonRpcProvider.
                                createCryptoJsonRpcResponse(method, emptyResult);


                        throw new HttpCallException("Can not continue recently!");
                    }
                }


            } catch (Exception e) {

                HashMap<String, Object> emptyResult = new HashMap<>();
                emptyResult.put("message", R.string.crash_error_message_unknown);

                cryptoJsonRpcResponse = JsonRpcProvider.
                        createCryptoJsonRpcResponse(method, emptyResult);
            }

            newResponse.body(cryptoJsonRpcResponse);
            return newResponse.build();
        }
        return response;
    }
}