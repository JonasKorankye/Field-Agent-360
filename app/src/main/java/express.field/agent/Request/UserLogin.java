package express.field.agent.Request;

import static express.field.agent.Request.RetrofitManager.refreshToken;
import static express.field.agent.Request.provider.JsonRpcProvider.getJsonRpc;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import express.field.agent.AgentApplication;
import express.field.agent.Request.net.BaseCall;
import express.field.agent.Request.net.JsonRpcCall;
import express.field.agent.Utils.Constants;
import express.field.agent.Utils.MapUtils;

/**
 * Created by myron echenim  on 8/14/16.
 */
abstract public class UserLogin {
    private HashMap<String, Object> mLoginParams = new HashMap<>();
    JsonRpcCall response;

    public void loginRequest(String password, String username) {
        String tag_json_obj = "login";

        String url = Constants.UrlConstant.login + username + "/" + password + "/";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        onLoginRequestComplete(response.optBoolean("Success"), response.optString("Message"));
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                onLoginRequestComplete(false, error.getMessage());
            }
        });

// Adding request to request queue
        AgentApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void loginRequestNew(String password, String username) {
        String tag_json_obj = "login";

        String url = Constants.UrlConstant.login;
        mLoginParams.put("lat", "5.6037168");
        mLoginParams.put("lng", "-0.1869644");
        mLoginParams.put("username", username);
        mLoginParams.put("password", password);
        mLoginParams.put("uri", "/login");
        mLoginParams.put("timezone", "+00:00");
        mLoginParams.put("channel", "web");

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, getJsonRpc("identity.check", mLoginParams),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject call) {
                        if (call instanceof JsonRpcCall) {
                            String actorId = null;
                            JsonRpcCall response = (JsonRpcCall) call;
                            Map<String, Object> result = (Map<String, Object>) response.getRequestResult();
                            System.out.println("result::" + result);

                            if (response.getMethod().equals("identity.check")) {
                                //store token
                                try {
                                    refreshToken(result);
                                } catch (GeneralSecurityException | IOException e) {
                                    e.printStackTrace();
                                }

                                if (MapUtils.getValue(Constants.LOGIN_RESPONSE_PERSON_ACTOR_ID, result) instanceof String) {
                                    actorId = (String) MapUtils.getValue(Constants.LOGIN_RESPONSE_PERSON_ACTOR_ID, result);
                                } else if (MapUtils.getValue(Constants.LOGIN_RESPONSE_PERSON_ACTOR_ID, result) instanceof Integer) {
                                    actorId = String.valueOf(MapUtils.getValue(Constants.LOGIN_RESPONSE_PERSON_ACTOR_ID, result));
                                }

                                if (actorId == null) {
                                    if (MapUtils.getValue(Constants.ERROR_RESPONSE_MESSAGE, result) != null) {
                                        String errorMessage = (String) MapUtils.getValue(Constants.ERROR_RESPONSE_MESSAGE, result);
                                        onLoginRequestComplete(false, errorMessage);

                                    }
                                }
                            }


                            VolleyLog.v("Response:%n %s", response.getJsonrpc());

                        }
                        onLoginRequestComplete(true, response.getMessage());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                onLoginRequestComplete(false, error.getMessage());
            }
        });

// Adding request to request queue
        AgentApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    abstract protected void onLoginRequestComplete(boolean status, String message);
}
