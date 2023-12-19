package express.field.agent.Request.net;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import express.field.agent.Request.exception.NetworkException;
import express.field.agent.Request.provider.JsonRpcProvider;
import express.field.agent.Request.provider.RestProvider;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Service to be used for any and all Network functionalities
 */
public class NetworkCall {

    /**
     * Implement this to be able to receive responses from your network calls
     * @param <T> Network API model instance , i.e. json-rpc call or Rest Call
     */
    public interface OnNetworkCallResultListener<T extends BaseCall> {
        void onNetworkCallResult(T call) throws GeneralSecurityException, IOException;

        void onNetworkCallException(NetworkException e);

        String getNetworkCallResultDeliveryTag();
    }

    public interface OnNetworkSyncCallResultListener<T extends BaseCall> {
        void onNetworkCallResult(T call);

        void onNetworkCallException(NetworkException e);
    }

    private static NetworkCall mInstance;

    public static NetworkCall getInstance() {
        if (mInstance == null) {
            synchronized (NetworkCall.class) {
                mInstance = new NetworkCall();
            }
        }

        return mInstance;
    }

    private NetworkCall() {
        mListeners = new HashMap<>();
    }

    private Map<String, OnNetworkCallResultListener> mListeners;

    /**
     * Register implementation to receive network call responses
     * @param listener
     */
    public void registerListener(OnNetworkCallResultListener listener) {
        if (listener != null) {
            mListeners.put(listener.getNetworkCallResultDeliveryTag(), listener);
        }
    }

    public void unregisterListener(OnNetworkCallResultListener listener) {
        if (listener != null) {
            mListeners.remove(listener.getNetworkCallResultDeliveryTag());
        }
    }

    public void unregisterListener(String tag) {
        mListeners.remove(tag);
    }

    /**
     * Makes a network call using the JsonRPC Provider.
     * The request will follow the RPC protocol pattern
     * and the result will contain both the request and the response/error
     * @param call
     * @param tag - delivery tag
     */
    public void makeJsonRpcCall(Call call, final String tag) {
        call.enqueue(new JsonRpcProvider() {
            @Override
            public void onResult(JsonRpcCall response) throws GeneralSecurityException, IOException {
                OnNetworkCallResultListener listener = mListeners.get(tag);
                if (listener != null) {
                    listener.onNetworkCallResult(response);

                }
            }

            @Override
            public void onException(NetworkException exception) {
                OnNetworkCallResultListener listener = mListeners.get(tag);
                if (listener != null) {
                    listener.onNetworkCallException(exception);

                }
            }
        });
    }

    /**
     * Makes a network call using the RestProvider
     * The request will follow the RESTful design style
     * The response will contain the result or the error
     * @param call
     * @param tag - delivery tag
     */
    public void makeRestCall(final Call call, final String tag) {
        call.enqueue(new RestProvider() {
            @Override
            public void onResult(RestCall response) throws GeneralSecurityException, IOException {
                OnNetworkCallResultListener listener = mListeners.get(tag);
                if (listener != null) {
                    listener.onNetworkCallResult(response);
                }
            }

            @Override
            public void onException(NetworkException t) {
                OnNetworkCallResultListener listener = mListeners.get(tag);
                if (listener != null) {
                    listener.onNetworkCallException(t);
                }
            }
        });
    }

    /**
     * Makes a network call using the RestProvider
     * The request will follow the RESTful design style
     * The response will contain the result or the error
     * @param call
     * @param tag - delivery tag
     * @param processId - id to be delivered with the result - used mainly to identify Tasks
     */
    public void makeRestCall(Call call, final String tag, final String processId) {
        call.enqueue(new RestProvider() {
            @Override
            public void onResult(RestCall response) throws GeneralSecurityException, IOException {
                OnNetworkCallResultListener listener = mListeners.get(tag);
                if (listener != null) {
                    response.setProcessId(processId);
                    listener.onNetworkCallResult(response);
                }
            }

            @Override
            public void onException(NetworkException t) {
                OnNetworkCallResultListener listener = mListeners.get(tag);
                if (listener != null) {
                    t.setId(processId);
                    listener.onNetworkCallException(t);
                }
            }
        });
    }

    /**
     * Makes a network call using the RestProvider
     * The request will follow the RESTful design style
     * The response will contain the result or the error
     * @param call
     * @param processId - id to be delivered with the result - used mainly to identify Tasks
     */
    public void makeSyncRestCall(Call call, final String processId, final OnNetworkSyncCallResultListener listener) {
        try {
            Response<RestCall> response = call.execute();
            RestProvider restProvider = new RestProvider() {
                @Override
                public void onResult(RestCall response) {
                    if (listener != null) {
                        response.setProcessId(processId);
                        listener.onNetworkCallResult(response);
                    }
                }

                @Override
                public void onException(NetworkException exception) {
                    if (listener != null) {
                        exception.setId(processId);
                        listener.onNetworkCallException(exception);
                    }
                }
            };

            restProvider.onResponse(call, response);
        } catch (IOException e) {
            e.printStackTrace();
            if (listener != null) {
                NetworkException exception = new NetworkException(e.getMessage());
                exception.setId(processId);
                listener.onNetworkCallException(exception);
            }
        }
    }

    /**
     * Makes a network call using the RestProvider
     * The request will follow the RESTful design style
     * The response will contain the result or the error
     * @param call
     */
    public void makeSyncRestCall(Call call, final OnNetworkSyncCallResultListener listener) {
        try {
            Response<RestCall> response = call.execute();
            RestProvider restProvider = new RestProvider() {
                @Override
                public void onResult(RestCall response) {
                    if (listener != null) {
                        listener.onNetworkCallResult(response);
                    }
                }

                @Override
                public void onException(NetworkException exception) {
                    if (listener != null) {
                        listener.onNetworkCallException(exception);
                    }
                }
            };

            restProvider.onResponse(call, response);
        } catch (IOException e) {
            e.printStackTrace();
            if (listener != null) {
                NetworkException exception = new NetworkException(e.getMessage());
                listener.onNetworkCallException(exception);
            }
        }
    }

    /**
     * Makes a network call using the JsonRPC Provider.
     * The request will follow the RPC protocol pattern
     * and the result will contain both the request and the response/error
     * @param call
     */
    public void makeSyncJsonRpcCall(Call call, final OnNetworkSyncCallResultListener listener) {
        try {
            Response<JsonRpcCall> response = call.execute();
            JsonRpcProvider jsonRpcProvider = new JsonRpcProvider() {
                @Override
                public void onResult(JsonRpcCall response) {
                    if (listener != null) {
                        listener.onNetworkCallResult(response);
                    }
                }

                @Override
                public void onException(NetworkException exception) {
                    if (listener != null) {
                        listener.onNetworkCallException(exception);
                    }
                }
            };

            jsonRpcProvider.onResponse(call, response);
        } catch (IOException e) {
            e.printStackTrace();
            if (listener != null) {
                NetworkException exception = new NetworkException(e.getMessage());
                listener.onNetworkCallException(exception);
            }
        }
    }

    public void makeSyncJsonRpcCall(Call call, String processId, final OnNetworkSyncCallResultListener listener) {
        try {
            Response<JsonRpcCall> response = call.execute();
            JsonRpcProvider jsonRpcProvider = new JsonRpcProvider() {
                @Override
                public void onResult(JsonRpcCall response) {
                    if (listener != null) {
                        response.setProcessId(processId);
                        listener.onNetworkCallResult(response);
                    }
                }

                @Override
                public void onException(NetworkException exception) {
                    if (listener != null) {
                        exception.setId(processId);
                        listener.onNetworkCallException(exception);
                    }
                }
            };

            jsonRpcProvider.onResponse(call, response);
        } catch (IOException e) {
            e.printStackTrace();
            if (listener != null) {
                NetworkException exception = new NetworkException(e.getMessage());
                exception.setId(processId);
                listener.onNetworkCallException(exception);
            }
        }
    }

    public JsonRpcCall makeSyncJsonRpcCall(Call call) throws NetworkException {
        try {
            Response<JsonRpcCall> response = call.execute();
            return JsonRpcProvider.onResponseSync(call, response);
        } catch (IOException e) {
            e.printStackTrace();
            throw new NetworkException(e.getMessage());
        }
    }

    public RestCall makeSyncRestCall(Call call) throws NetworkException {
        try {
            Response<RestCall> response = call.execute();
            return RestProvider.onResponseSync(call, response);
        } catch (IOException | NetworkException e) {
            e.printStackTrace();
            throw new NetworkException(e.getMessage());
        }
    }

}
