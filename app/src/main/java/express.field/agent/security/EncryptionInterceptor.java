package express.field.agent.security;


import android.util.Log;

import androidx.annotation.NonNull;


import java.io.IOException;

import express.field.agent.Pref.GlobalPref;
import express.field.agent.Request.exception.CryptoException;
import express.field.agent.Request.provider.JsonRpcProvider;
import express.field.agent.Utils.Constants;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Retrofit Interceptor to intercept and encrypt response from the server
 */
public class EncryptionInterceptor implements Interceptor {
    private static final String KEY_ALIAS = "MLEK_EC";

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        RequestBody rawBody = request.body();

        String method = GlobalPref.getInstance().getString(Constants.METHOD, "");


        String encryptedBody = null;
        MediaType mediaType = MediaType.parse("text/plain; charset=utf-8");

        Log.i("Raw BODY=> %s", request.body().toString());

        try {
            encryptedBody = Crypto.preparePayload(rawBody.toString());
        } catch (CryptoException e) {
            e.printStackTrace();
        }


        Log.i("Encrypted BODY=> %s", String.valueOf(encryptedBody));

        //            try {
//                char[] rawBodyString = KeyStoreUtil.encryptString(rawBody.toString(), KEYSTORE_ALIAS);
//                encryptedBody = new String(rawBodyString);
//
//                Log.i("Encrypted BODY=> %s", encryptedBody);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        final RequestBody cryptoJsonRpcRequest = JsonRpcProvider.createCryptoJsonRpcRequest(method, encryptedBody);


//        RequestBody body = RequestBody.create(mediaType, encryptedBody);//build new request
        request = request.
            newBuilder()
            .header("Content-Type", cryptoJsonRpcRequest.contentType().toString())
            .header("Content-Length", String.valueOf(cryptoJsonRpcRequest.contentLength()))
            .method(request.method(), cryptoJsonRpcRequest).build();
        return chain.proceed(request);

    }
}
