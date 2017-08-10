package com.example.lenovo.wheelcare;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Module Name: Authentication Manager
 * Date: 05-08-2017.
 * Author: Vimal Gohel
 * Company: At Application
 * Description: This is the generic implementation of Androids Authentication Framework
 *
 * Default Configuration:
 * - AuthenticationType = OAUTH
 *
 * Note:
 * User should initialize some of variables
 * LoginURL
 * AuthenticationType
 * - If AuthenticationType = OAUTH, please initialize these variables:
 * - accessTokenKey, accessTokenExpirationDateKey, baseTokenKey, and baseTokenExpirationDateKey.
 * - If these variable are not initialized , default value will be considered
 * - In case of OAUTH, the token will be renewed automatically by this class
 *
 * Setting the SessionValidity has to be handled by the package utilizing this module.
 */

// TODO: Renewing token flow
// TODO: Make Token class for handling all the token related stuffs

// MARK: Enums

enum AuthenticationType {
    NORMAL,
    SSO,
    OAUTH
}

enum SessionValidity {
    INVALID,
    VALID
}

public class AuthenticationManager {

    // MARK: Singleton declarations

    private static AuthenticationManager ourInstance;

    // MARK: Class related declarations

    private static final String TAG = AuthenticationManager.class.getSimpleName();

    private static final String SUCCESS = "200";

    private String loginURL;

    private String registerURL;

    private String renewURL;

    private boolean isSessionValid = false;

    private AuthenticationType authenticationType = AuthenticationType.OAUTH;

    private String userID;

    // MARK: For Token Based communication

    private String accessToken;

    private String baseToken;

    private Long accessTokenExpirationDate;

    private Long baseTokenExpirationDate;

    private String accessTokenKey = "accessToken";

    private String accessTokenExpirationDateKey = "accessTokenExpDate";

    private String baseTokenKey = "baseToken";

    private String baseTokenExpirationDateKey = "baseTokenExpDate";

    // MARK: Initializer

    public static AuthenticationManager getInstance() {
        if(ourInstance == null) {
            ourInstance = new AuthenticationManager();
        }
        return ourInstance;
    }

    private AuthenticationManager() {

    }

    // MARK: Initialization Function

    public void setLoginURL(String loginURL) {
        this.loginURL = loginURL;
    }

    public void setRegisterURL(String registerURL) {
        this.registerURL = registerURL;
    }

    public void setRenewURL(String renewURL) {
        this.renewURL = renewURL;
    }

    public void setAuthenticationType(AuthenticationType authenticationType) {
        this.authenticationType = authenticationType;
    }

    public AuthenticationType getAuthenticationType() {
        return authenticationType;
    }

    public void setSession(SessionValidity validity) {
        this.isSessionValid = (validity == SessionValidity.VALID);
    }

    private void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserID() {
        return this.userID;
    }

    // MARK: For Token Based communication

    public void setAccessTokenKey(String accessTokenKey) {
        this.accessTokenKey = accessTokenKey;
    }

    public void setAccessTokenExpirationDateKey(String accessTokenExpirationDateKey) {
        this.accessTokenExpirationDateKey = accessTokenExpirationDateKey;
    }

    public void setBaseTokenKey(String baseTokenKey) {
        this.baseTokenKey = baseTokenKey;
    }

    public void setBaseTokenExpirationDateKey(String baseTokenExpirationDateKey) {
        this.baseTokenExpirationDateKey = baseTokenExpirationDateKey;
    }

    private void setAccessToken(String token, Long expiryTime) {
        this.accessToken = token;
        this.accessTokenExpirationDate = expiryTime;
    }

    public String getAccessToken() {
        return this.accessToken;
        // TODO: Add code to check if access token has expired
    }

    private Long getAccessTokenExpiry() {
        return this.accessTokenExpirationDate;
    }

    private void setBaseToken(String token, Long expiryTime) {
        this.baseToken = token;
        this.baseTokenExpirationDate = expiryTime;
    }

    public String getBaseToken() {
        return this.baseToken;
        // TODO: Add code to check if base token has expired
    }

    private Long getBaseTokenExpiry() {
        return this.baseTokenExpirationDate;
    }

    // MARK: Common Related Functions

    public SessionValidity isSessionValid() {
        return this.isSessionValid ? SessionValidity.VALID : SessionValidity.INVALID;
    }

    // MARK: Web Services related Functions

    public void login(Context context, final LoginListener callback, final JSONObject object) {

        // Add the request to the RequestQueue.
        WebServiceManager.getInstance(context).addToRequestQueue(
            // Request a string response from the provided URL.
            new JsonObjectRequest(Request.Method.POST, loginURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            if (Objects.equals((String) response.get("statusCode"), SUCCESS)) {
                                Log.d(TAG, "Inside");
                                AuthenticationType type = getAuthenticationType();
                                switch(type) {
                                    case NORMAL: break;
                                    case SSO: break;
                                    default:
                                        setAccessToken((String) response.get(accessTokenKey), (Long) response.get(accessTokenExpirationDateKey));
                                        setAccessToken((String) response.get(baseTokenKey), (Long) response.get(baseTokenExpirationDateKey));
                                }
                                setUserID((String) response.get("userId"));
                                callback.loginSuccess(response);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Got error");
                        callback.loginFailed(error);
                    }
                }
            ) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() {
                    try {
                        return object == null ? null : object.toString().getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", object.toString(), "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                    Log.d(TAG, String.valueOf(response.statusCode));
                    return super.parseNetworkResponse(response);
                }
            }
        );
    }

    public void register(Context context, final RegistrationListener callback, final JSONObject object) {

        // Add the request to the RequestQueue.
        WebServiceManager.getInstance(context).addToRequestQueue(
                // Request a string response from the provided URL.
                new JsonObjectRequest(Request.Method.POST, registerURL, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, response.toString());
                                try {
                                    if (Objects.equals((String) response.get("statusCode"), SUCCESS)) {
                                        Log.d(TAG, "Inside");
                                        callback.registrationSuccessful();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Got error");
                                callback.registrationFailed(error);
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String,String> header = new HashMap<>();
                        header.put("X-ACCESS-TOKEN", getAccessToken());
                        Log.e(TAG,"header "+header);
                        return header;
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() {
                        try {
                            return object == null ? null : object.toString().getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", object.toString(), "utf-8");
                            return null;
                        }
                    }

                    @Override
                    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                        Log.d(TAG, String.valueOf(response.statusCode));
                        return super.parseNetworkResponse(response);
                    }
                }
        );
    }

    public void renewToken(Context context) {

        final JSONObject object = new JSONObject();
        try {
            object.put("userId", this.getUserID());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        // Add the request to the RequestQueue.
        WebServiceManager.getInstance(context).addToRequestQueue(
                // Request a string response from the provided URL.
                new JsonObjectRequest(Request.Method.POST, renewURL, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, response.toString());
                                try {
                                    if (Objects.equals((String) response.get("statusCode"), SUCCESS)) {
                                        Log.d(TAG, "Inside");

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Got error");

                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String,String> header = new HashMap<>();
                        header.put("X-BASE-TOKEN", getAccessToken());
                        Log.e(TAG,"header "+header);
                        return header;
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() {
                        try {
                            return object.toString().getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", object.toString(), "utf-8");
                            return null;
                        }
                    }

                    @Override
                    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                        Log.d(TAG, String.valueOf(response.statusCode));
                        return super.parseNetworkResponse(response);
                    }
                }
        );
    }
}