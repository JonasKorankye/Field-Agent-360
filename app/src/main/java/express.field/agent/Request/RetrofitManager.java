package express.field.agent.Request;

import static android.os.Build.VERSION_CODES.R;
import static express.field.agent.Utils.Constants.KEYSTORE_ALIAS;
import static okhttp3.internal.platform.Platform.INFO;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;


import org.jetbrains.annotations.NotNull;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import express.field.agent.Helpers.ContextProvider;
import express.field.agent.Helpers.ResourceProvider;
import express.field.agent.Pref.GlobalPref;
import express.field.agent.R;
import express.field.agent.Request.exception.NetworkException;
import express.field.agent.Request.net.NetworkCall;
import express.field.agent.Request.net.OnDownloadListener;
import express.field.agent.Request.net.ProgressResponseBodyWrapper;
import express.field.agent.Request.net.api.V2BaseApi;
import express.field.agent.Utils.Constants;
import express.field.agent.Utils.KeyStoreUtil;
import express.field.agent.Utils.ObjectUtils;
import okhttp3.Authenticator;
import okhttp3.CertificatePinner;
import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.Route;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.platform.Platform;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Manages the services and the APIs the app will connect to.
 */
public class RetrofitManager {

    private static HashMap<String, Retrofit> urlInstanceCache = new HashMap<>();
    private static HashMap<String, Object> apiServiceInstanceCache = new HashMap<>();

    private static Timer tokenTimer;
    private static volatile AtomicBoolean tokenValidity = new AtomicBoolean(false);
    private static HashMap<String, String> tokens = new HashMap<>();


    public static Retrofit getBaseUrlRetrofitInstance(String url) {
        return getRetrofitInstance(url);
    }


    public static V2BaseApi getV2BaseApiService() {
        String url = Constants.BASE_URL;

        if (apiServiceInstanceCache.get(V2BaseApi.class.getName()) == null) {
            apiServiceInstanceCache.put(V2BaseApi.class.getName(),
                getBaseUrlRetrofitInstance(url).create(V2BaseApi.class));
        }

        return (V2BaseApi) apiServiceInstanceCache.get(V2BaseApi.class.getName());
    }

    public static V2BaseApi getV2RawBaseApiService() {
        String url = Constants.BASE_URL;

        if (apiServiceInstanceCache.get(V2BaseApi.class.getName()) == null) {
            apiServiceInstanceCache.put(V2BaseApi.class.getName(),
                getBaseUrlRetrofitInstance(url).create(V2BaseApi.class));
        }

        return (V2BaseApi) apiServiceInstanceCache.get(V2BaseApi.class.getName());
    }



    public static final V2BaseApi getDownloadService(OnDownloadListener listener) {
        if (apiServiceInstanceCache.get(Constants.DOWNLOAD_API_SERVICE) == null) {
            apiServiceInstanceCache.put(Constants.DOWNLOAD_API_SERVICE,
                getDownloadRetrofitInstance(listener).create(V2BaseApi.class));
        }

        return (V2BaseApi) apiServiceInstanceCache.get(Constants.DOWNLOAD_API_SERVICE);
    }

    private static Retrofit getDownloadRetrofitInstance(OnDownloadListener listener) {
        return new Retrofit.Builder()
            .baseUrl("http://localhost:8080")
            .addConverterFactory(JacksonConverterFactory.create())
            .client(getOkHttpDownloadClientBuilder(listener).build())
            .build();
    }

    private static OkHttpClient.Builder getOkHttpDownloadClientBuilder(OnDownloadListener progressListener) {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        httpClientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                if (progressListener == null) return chain.proceed(chain.request());

                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder()
                    .body(new ProgressResponseBodyWrapper(originalResponse.body(), progressListener))
                    .build();
            }
        }).connectTimeout(15, TimeUnit.SECONDS)
            .callTimeout(360, TimeUnit.SECONDS)
            .writeTimeout(360, TimeUnit.SECONDS)
            .readTimeout(360, TimeUnit.SECONDS);

        return httpClientBuilder;
    }

    private static Retrofit getRetrofitInstance(String url) {
        if (urlInstanceCache.get(url) == null) {
            Retrofit instance = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(JacksonConverterFactory.create(ObjectUtils.getMapper()))
                .client(getOkHttpWithCertificates()
//                    .addInterceptor(new DecryptionInterceptor())
                    .addInterceptor(new SendInterceptor())
                    .addInterceptor(new ReceiveInterceptor())
                    .authenticator(new TokenAuthenticator())
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .callTimeout(360, TimeUnit.SECONDS)
                    .writeTimeout(360, TimeUnit.SECONDS)
                    .readTimeout(360, TimeUnit.SECONDS).build())

                .build();

            urlInstanceCache.put(url, instance);
        }

        return urlInstanceCache.get(url);
    }


    private static OkHttpClient.Builder getOkHttpWithCertificates() {
        String env = Constants.ENVIRONMENT;
        OkHttpClient.Builder okhttpBuilder = new OkHttpClient.Builder();

        CertificatePinner.Builder certificatePinnerBuilder = new CertificatePinner.Builder()
            .add("agency.nmbbank.co.tz",
                "sha256/PxN3di8ZNCZ43HCAMBIeG54roXgH6s7A59XxHNv85UI=",
                "sha256/Wec45nQiFwKvHtuHxSAMGkt19k+uPSw9JlEkxhvYPHk=",
                "sha256/i7WTqTvh0OioIruIfFR4kMPnBqrS2rdiVPl/s2uC/CY=");

        if (env.equalsIgnoreCase("UAT")) {
            certificatePinnerBuilder.add("agencytest.nmbbank.co.tz",
                "sha256/PxN3di8ZNCZ43HCAMBIeG54roXgH6s7A59XxHNv85UI=",
                "sha256/Wec45nQiFwKvHtuHxSAMGkt19k+uPSw9JlEkxhvYPHk=",
                "sha256/i7WTqTvh0OioIruIfFR4kMPnBqrS2rdiVPl/s2uC/CY=");
        }

        if (env.equalsIgnoreCase("Test")) {
            certificatePinnerBuilder.add("nmb-qa.softwaregroup.com",
                "sha256/2EhMK0VlpcrytZc3l9VXrD7688+Q1NxDaBT40NHGISA=",
                "sha256/xsp6ItA5eh33nkUVgnOwilj8y1/leyqb0v1TwBBZy6Y=");


            //NOTE: only include when Charles Proxy is required
            //certificatePinnerBuilder
            //    .add("charlesproxy.com/ssl",
            //       "sha256/47DEQpj8HBSa+/TImW+5JCeuQeRkm5NMpJWZG3hSuFU=",
            //        "sha256/+ORvVGko4RuQVc1JG1iY829rm/BzAYNpDEjBP28YO4w="
            //    );


            InputStream qaCertFile = null;

            //NOTE: only include when Charles Proxy is required
            //InputStream charlesCertFile = null;

            try {
                Context context = ContextProvider.getInstance().getContext();

                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                qaCertFile = context.getResources().openRawResource(express.field.agent.R.raw.cert);
                Certificate qaCertificate = cf.generateCertificate(qaCertFile);

                // NOTE: only include when Charles Proxy is required
                //charlesCertFile = context.getResources().openRawResource(R.raw.charles);
                //Certificate charlesCertificate = cf.generateCertificate(charlesCertFile);

                String keyStoreType = KeyStore.getDefaultType();
                KeyStore keyStore = KeyStore.getInstance(keyStoreType);
                keyStore.load(null, null);
                keyStore.setCertificateEntry("qa", qaCertificate);

                //NOTE: only include when Charles Proxy is required
                //keyStore.setCertificateEntry("charles", charlesCertificate);

                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                tmf.init(keyStore);

                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, tmf.getTrustManagers(), new java.security.SecureRandom());

                okhttpBuilder.sslSocketFactory(sslContext.getSocketFactory());
                okhttpBuilder.hostnameVerifier((hostname, session) -> true);
            } catch (CertificateException | KeyStoreException | IOException | NoSuchAlgorithmException | KeyManagementException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    if (qaCertFile != null) qaCertFile.close();

                    // NOTE: only include when Charles Proxy is required
                    //if (charlesCertFile != null) charlesCertFile.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        CertificatePinner certificatePinner = certificatePinnerBuilder.build();
        return okhttpBuilder.certificatePinner(certificatePinner);
    }

    /**
     * Interceptor to attach custom headers and cookies
     * //TODO Extend to distinguish between services
     */
    private static class SendInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException, IllegalStateException {
            Request.Builder builder = chain.request().newBuilder();
            if (!chain.request().url().toString().contains("login/identity/exchange") && !chain.request().url().toString().contains("/custom/token")) {
                if (!tokenValidity.get()) {
                    authenticate();
                }
                if (tokens.containsKey(Constants.ACCESS_TOKEN)) {
                    builder.addHeader(Constants.AUTHORIZATION, Constants.BEARER + tokens.get(Constants.ACCESS_TOKEN));
                }
            }

            Request request = chain.request();
            Connection connection = chain.connection();
            RequestBody requestBody = request.body();
            boolean hasRequestBody = requestBody != null;

            String requestStartMessage = "--> "
                + request.method()
                + ' ' + request.url()
                + (connection != null ? " " + connection.protocol() : "");
            if (hasRequestBody) {
                requestStartMessage += " (" + requestBody.contentLength() + "-byte body)";
            }

            Log.d("Request size --->", requestStartMessage);

            return chain.proceed(builder.build());
        }
    }

    /**
     * Intercept to store cookies
     * //TODO Extend to distinguish between services
     */
    private static class ReceiveInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());

            try {
                if (originalResponse.request().url().toString().contains("/login/identity/exchange") || originalResponse.request().url().toString().contains("/custom/identity/changePassword")) {
                    BufferedSource source = originalResponse.body().source();
                    source.request(Long.MAX_VALUE);
                    Buffer buffer = source.getBuffer();
                    String responseBodyString = buffer.clone().readString(Charset.forName("UTF-8"));
                    Map<String, Object> resp = ObjectUtils.jsonToMap(responseBodyString.getBytes());
                    if (originalResponse.request().url().toString().contains("/custom/identity/changePassword")
                        && (((Map) resp.get("result")).containsKey("isSecretQuestionSet") && ((Map) resp.get("result")).get("isSecretQuestionSet").equals(false))) {
                        if (resp.containsKey("result")) {
                            Map<String, Object> result = (Map<String, Object>) resp.get("result");
                            refreshToken(result);
                        }
                    } else {
                        if (resp.containsKey("result")) {
                            Map<String, Object> result = (Map<String, Object>) resp.get("result");
                            refreshToken(result);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return originalResponse;
        }
    }

    /**
     * Optimization function to keep track the session locally on the device. Used to avoid duplicating a request with an expired token only to receive a 401
     *
     * @param sess returned from backend - must contain the following params : "access_token", "expires_in", "refresh_token"
     */
    public static boolean refreshToken(Map<String, Object> sess) throws GeneralSecurityException, IOException {
        if (sess.containsKey(Constants.ACCESS_TOKEN) && sess.containsKey(Constants.EXPIRES_IN) && sess.containsKey(Constants.REFRESH_TOKEN)) {
            tokens.put(Constants.ACCESS_TOKEN, (String) sess.get(Constants.ACCESS_TOKEN));

            char[] tokenArray = KeyStoreUtil.encryptStringSym((String) sess.get(Constants.REFRESH_TOKEN), KEYSTORE_ALIAS);

            if (tokenArray == null || tokenArray.length == 0) {
                tokenArray = (char[]) sess.get(Constants.REFRESH_TOKEN);
            }

            GlobalPref.getInstance().put(Constants.REFRESH_TOKEN, String.valueOf(tokenArray));

            if (tokenTimer != null) {
                tokenTimer.cancel();
                tokenTimer = null;
            }

            tokenValidity.set(true);

            tokenTimer = new Timer();
            tokenTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    tokenValidity.set(false);
                }
            }, (int) sess.get(Constants.EXPIRES_IN) * 1000);

            return true;
        }

        return false;
    }

    private static boolean authenticate() {
        try {
            Map<String, Object> params = new HashMap<>();

            String refreshToken = GlobalPref.getInstance().getString(Constants.REFRESH_TOKEN, "");
            if (TextUtils.isEmpty(refreshToken)) {
                return false;
            }
            char[] decryptedRefreshToken = KeyStoreUtil.decryptStringSym(refreshToken, KEYSTORE_ALIAS);
            if (decryptedRefreshToken == null || decryptedRefreshToken.length == 0) {
                decryptedRefreshToken = refreshToken.toCharArray();
            }

            params.put(Constants.GRANT_TYPE, Constants.REFRESH_TOKEN);
            params.put(Constants.REFRESH_TOKEN, String.valueOf(decryptedRefreshToken));
            Call call = RetrofitManager.getV2BaseApiService().refreshToken(params);

            Map<String, Object> result = (Map<String, Object>) NetworkCall.getInstance().makeSyncRestCall(call).getRequestResult();
            return refreshToken(result);
        } catch (NetworkException | GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Authenticator to handle refreshing of the access token and repeat the request
     * If the Refresh Token is expired or an error is thrown it should bubble up to the initial request
     * //TODO Extend to distinguish between services
     */
    private static class TokenAuthenticator implements Authenticator {
        @Override
        public Request authenticate(Route route, @NotNull Response response) {
            if (RetrofitManager.authenticate()) {
                return response
                    .request()
                    .newBuilder()
                    .header(Constants.AUTHORIZATION, Constants.BEARER + tokens.get(Constants.ACCESS_TOKEN))
                    .build();
            }

            return null;
        }
    }

    private static class LogInterceptor implements Interceptor {

        private static final Charset UTF8 = Charset.forName("UTF-8");

        public enum Level {
            /**
             * No logs.
             */
            NONE,
            /**
             * Logs request and response lines.
             *
             * <p>Example:
             * <pre>{@code
             * --> POST /greeting http/1.1 (3-byte body)
             *
             * <-- 200 OK (22ms, 6-byte body)
             * }</pre>
             */
            BASIC,
            /**
             * Logs request and response lines and their respective headers.
             *
             * <p>Example:
             * <pre>{@code
             * --> POST /greeting http/1.1
             * Host: example.com
             * Content-Type: plain/text
             * Content-Length: 3
             * --> END POST
             *
             * <-- 200 OK (22ms)
             * Content-Type: plain/text
             * Content-Length: 6
             * <-- END HTTP
             * }</pre>
             */
            HEADERS,
            /**
             * Logs request and response lines and their respective headers and bodies (if present).
             *
             * <p>Example:
             * <pre>{@code
             * --> POST /greeting http/1.1
             * Host: example.com
             * Content-Type: plain/text
             * Content-Length: 3
             *
             * Hi?
             * --> END POST
             *
             * <-- 200 OK (22ms)
             * Content-Type: plain/text
             * Content-Length: 6
             *
             * Hello!
             * <-- END HTTP
             * }</pre>
             */
            BODY
        }

        public interface Logger {
            void log(String message);

            Logger DEFAULT = new Logger() {
                @Override
                public void log(String message) {
                    Platform.get().log(INFO, message, null);
                }
            };
        }

        public LogInterceptor() {
            this(Logger.DEFAULT);
        }

        public LogInterceptor(Logger logger) {
            this.logger = logger;
        }

        private final Logger logger;

        private volatile Level level = Level.NONE;

        /**
         * Change the level at which this interceptor logs.
         */
        public LogInterceptor setLevel(Level level) {
            if (level == null)
                throw new NullPointerException("level == null. Use Level.NONE instead.");
            this.level = level;
            return this;
        }

        public Level getLevel() {
            return level;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Level level = this.level;

            Request request = chain.request();
            if (level == Level.NONE) {
                return chain.proceed(request);
            }

            boolean logBody = level == Level.BODY;
            boolean logHeaders = logBody || level == Level.HEADERS;

            RequestBody requestBody = request.body();
            boolean hasRequestBody = requestBody != null;

            Connection connection = chain.connection();
            String requestStartMessage = "--> "
                + request.method()
                + ' ' + request.url()
                + (connection != null ? " " + connection.protocol() : "");
            if (!logHeaders && hasRequestBody) {
                requestStartMessage += " (" + requestBody.contentLength() + "-byte body)";
            }
            logger.log(requestStartMessage);

            if (logHeaders) {
                if (hasRequestBody) {
                    // Request body headers are only present when installed as a network interceptor. Force
                    // them to be included (when available) so there values are known.
                    if (requestBody.contentType() != null) {
                        logger.log("Content-Type: " + requestBody.contentType());
                    }
                    if (requestBody.contentLength() != -1) {
                        logger.log("Content-Length: " + requestBody.contentLength());
                    }
                }

                Headers headers = request.headers();
                for (int i = 0, count = headers.size(); i < count; i++) {
                    String name = headers.name(i);
                    // Skip headers from the request body as they are explicitly logged above.
                    if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                        logger.log(name + ": " + headers.value(i));
                    }
                }

                if (!logBody || !hasRequestBody) {
                    logger.log("--> END " + request.method());
                } else if (bodyEncoded(request.headers())) {
                    logger.log("--> END " + request.method() + " (encoded body omitted)");
                } else {
                    Buffer buffer = new Buffer();
                    requestBody.writeTo(buffer);

                    Charset charset = UTF8;
                    MediaType contentType = requestBody.contentType();
                    if (contentType != null) {
                        charset = contentType.charset(UTF8);
                    }

                    logger.log("");
                    if (isPlaintext(buffer)) {
                        logger.log(buffer.readString(charset));
                        logger.log("--> END " + request.method()
                            + " (" + requestBody.contentLength() + "-byte body)");
                    } else {
                        logger.log("--> END " + request.method() + " (binary "
                            + requestBody.contentLength() + "-byte body omitted)");
                    }
                }
            }

            long startNs = System.nanoTime();
            Response response;
            try {
                response = chain.proceed(request);
            } catch (Exception e) {
                logger.log("<-- HTTP FAILED: " + e);
                throw new IOException(ResourceProvider.getString("technical_connection_error"));
            }
            long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

            ResponseBody responseBody = response.body();
            long contentLength = responseBody.contentLength();
            String bodySize = contentLength != -1 ? contentLength + "-byte" : "unknown-length";
            logger.log("<-- "
                + response.code()
                + (response.message().isEmpty() ? "" : ' ' + response.message())
                + ' ' + response.request().url()
                + " (" + tookMs + "ms" + (!logHeaders ? ", " + bodySize + " body" : "") + ')');

            if (logHeaders) {
                Headers headers = response.headers();
                for (int i = 0, count = headers.size(); i < count; i++) {
                    logger.log(headers.name(i) + ": " + headers.value(i));
                }

                if (!logBody || !HttpHeaders.hasBody(response)) {
                    logger.log("<-- END HTTP");
                } else if (bodyEncoded(response.headers())) {
                    logger.log("<-- END HTTP (encoded body omitted)");
                } else {
                    BufferedSource source = responseBody.source();
                    source.request(Long.MAX_VALUE); // Buffer the entire body.
                    Buffer buffer = source.buffer();

                    Charset charset = UTF8;
                    MediaType contentType = responseBody.contentType();
                    if (contentType != null) {
                        charset = contentType.charset(UTF8);
                    }

                    if (!isPlaintext(buffer)) {
                        logger.log("");
                        logger.log("<-- END HTTP (binary " + buffer.size() + "-byte body omitted)");
                        return response;
                    }

                    if (contentLength != 0) {
                        logger.log("");
                        logger.log(buffer.clone().readString(charset));
                    }

                    logger.log("<-- END HTTP (" + buffer.size() + "-byte body)");
                }
            }

            return response;
        }

        /**
         * Returns true if the body in question probably contains human readable text. Uses a small sample
         * of code points to detect unicode control characters commonly used in binary file signatures.
         */
        static boolean isPlaintext(Buffer buffer) {
            try {
                Buffer prefix = new Buffer();
                long byteCount = buffer.size() < 64 ? buffer.size() : 64;
                buffer.copyTo(prefix, 0, byteCount);
                for (int i = 0; i < 16; i++) {
                    if (prefix.exhausted()) {
                        break;
                    }
                    int codePoint = prefix.readUtf8CodePoint();
                    if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                        return false;
                    }
                }
                return true;
            } catch (EOFException e) {
                return false; // Truncated UTF-8 sequence.
            }
        }

        private boolean bodyEncoded(Headers headers) {
            String contentEncoding = headers.get("Content-Encoding");
            return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
        }
    }

}
