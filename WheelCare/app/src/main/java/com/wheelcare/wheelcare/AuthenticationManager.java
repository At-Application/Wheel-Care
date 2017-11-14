package com.wheelcare.wheelcare;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

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

    private String forgotPasswordURL;

    private String changePasswordURL;

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

    private Timer timer;

    private TimerTask timerTask;

    private static long PriorTime = (1000 * 60 * 5); // 5 min

    private String Screen = "MainActivity";

    GlobalClass globalClass;

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


    public String getScreen() {
        SharedPreferences preferences = globalClass.getApplicationContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        this.Screen = preferences.getString("Screen", "MainActivity");
        return this.Screen;
    }

    public void setMainScreen(String Screen) {
        this.Screen = Screen;
        SharedPreferences preferences = globalClass.getApplicationContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Screen", Screen);
        editor.apply();
    }

    public void setLoginURL(String loginURL) {
        this.loginURL = loginURL;
    }

    public void setRegisterURL(String registerURL) {
        this.registerURL = registerURL;
    }

    public void setRenewURL(String renewURL) {
        this.renewURL = renewURL;
    }

    public void setForgotPasswordURL(String forgotPasswordURL) { this.forgotPasswordURL = forgotPasswordURL; }

    public void setChangePasswordURL(String changePasswordURL) {
        this.changePasswordURL = changePasswordURL;
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
        SharedPreferences preferences = globalClass.getApplicationContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userID", userID);
        editor.apply();
    }

    public String getUserID() {
        SharedPreferences preferences = globalClass.getApplicationContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        this.userID = preferences.getString("userID", null);
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
        SharedPreferences preferences = globalClass.getApplicationContext().getSharedPreferences("AccessToken", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", token);
        editor.putLong("expiry", expiryTime);
        editor.apply();
    }

    public String getAccessToken() {
        SharedPreferences preferences = globalClass.getApplicationContext().getSharedPreferences("AccessToken", Context.MODE_PRIVATE);
        this.accessToken = preferences.getString("token", null);
        return this.accessToken;
        // TODO: Add code to check if access token has expired
    }

    private Long getAccessTokenExpiry() {
        SharedPreferences preferences = globalClass.getApplicationContext().getSharedPreferences("AccessToken", Context.MODE_PRIVATE);
        this.accessTokenExpirationDate = preferences.getLong("expiry", 0);
        return this.accessTokenExpirationDate;
    }

    private boolean isTokenExpired(Long expiryTime) {
        if(expiryTime == null) return true;

        Date expiry = new Date(expiryTime);
        Date current = new Date();
        if(expiry.getTime() < current.getTime()) {
            return true;
        }
        return false;
    }

    public void startTokenExpiryCheck() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if(isTokenExpired(getBaseTokenExpiry())) {
                    // Base Token is expired. User has to login again.
                    // This situation will never come as new Base token is generated while renewing
                } else {
                    // Base Token not expired. Renew the session
                    renewToken(globalClass.getApplicationContext());
                }
            }
        };

        if(getUserID() != null) {
            if(isTokenExpired(getAccessTokenExpiry())) {
                if(isTokenExpired(getBaseTokenExpiry())) {
                    setSession(SessionValidity.INVALID);
                } else {
                    renewToken(globalClass.getApplicationContext());
                }
            } else {
                Long current = new Date().getTime();
                timer = new Timer();
                timer.schedule(timerTask, (getAccessTokenExpiry() - current - PriorTime));
            }
        } else {
            setSession(SessionValidity.INVALID);
        }
    }

    public void stopTimer() {
        timer.cancel();
    }

    private void setBaseToken(String token, Long expiryTime) {
        this.baseToken = token;
        this.baseTokenExpirationDate = expiryTime;
        SharedPreferences preferences = globalClass.getApplicationContext().getSharedPreferences("BaseToken", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", token);
        editor.putLong("expiry", expiryTime);
        editor.apply();
    }

    public String getBaseToken() {
        SharedPreferences preferences = globalClass.getApplicationContext().getSharedPreferences("BaseToken", Context.MODE_PRIVATE);
        this.baseToken = preferences.getString("token", null);
        return this.baseToken;
        // TODO: Add code to check if base token has expired
    }

    private Long getBaseTokenExpiry() {
        SharedPreferences preferences = globalClass.getApplicationContext().getSharedPreferences("BaseToken", Context.MODE_PRIVATE);
        this.baseTokenExpirationDate = preferences.getLong("expiry", 0);
        return this.baseTokenExpirationDate;
    }

    // MARK: Common Related Functions

    public SessionValidity isSessionValid() {
        return this.isSessionValid ? SessionValidity.VALID : SessionValidity.INVALID;
    }

    // MARK: Web Services related Functions

    public void logout(final LogoutListener callback) {
        SharedPreferences preferences = globalClass.getApplicationContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        preferences = globalClass.getApplicationContext().getSharedPreferences("AccessToken", Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.clear();
        editor.apply();

        preferences = globalClass.getApplicationContext().getSharedPreferences("BaseToken", Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.clear();
        editor.apply();

        preferences = globalClass.getApplicationContext().getSharedPreferences("CarList", Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.clear();
        editor.apply();

        this.stopTimer();

        WebServiceManager.getInstance(globalClass.getApplicationContext()).getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });

        callback.logoutSuccess();
    }

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
                                        setBaseToken((String) response.get(baseTokenKey), (Long) response.get(baseTokenExpirationDateKey));
                                }
                                setUserID((String) response.get("userId"));
                                setSession(SessionValidity.VALID);
                                startTokenExpiryCheck();
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

        if(!((GlobalClass)context).isConnected) {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show();
            return;
        }

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
                                        AuthenticationType type = getAuthenticationType();
                                        switch(type) {
                                            case NORMAL: break;
                                            case SSO: break;
                                            default:
                                                setAccessToken((String) response.get(accessTokenKey), (Long) response.get(accessTokenExpirationDateKey));
                                                setBaseToken((String) response.get(baseTokenKey), (Long) response.get(baseTokenExpirationDateKey));
                                        }
                                        setSession(SessionValidity.VALID);
                                        startTokenExpiryCheck();
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
                        header.put("X-BASE-TOKEN", getBaseToken());
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

    public void forgotPassword(Context context, final ForgotPasswordListener listener, final JSONObject object) {

        // Add the request to the RequestQueue.
        WebServiceManager.getInstance(context).addToRequestQueue(
                // Request a string response from the provided URL.
                new JsonObjectRequest(Request.Method.POST, forgotPasswordURL, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                listener.ResponseSuccess(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Got error");
                                listener.ResponseFailure(error);
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
                        try {
                            String jsonString = new String(response.data,
                                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));

                            JSONObject result = null;

                            if (jsonString != null && jsonString.length() > 0)
                                result = new JSONObject(jsonString);

                            return Response.success(result,
                                    HttpHeaderParser.parseCacheHeaders(response));
                        } catch (UnsupportedEncodingException e) {
                            return Response.error(new ParseError(e));
                        } catch (JSONException je) {
                            return Response.error(new ParseError(je));
                        }
                    }
                }
        );
    }

    public void changePassword(Context context, final ChangePasswordListener listener, final JSONObject object) {

        // Add the request to the RequestQueue.
        WebServiceManager.getInstance(context).addToRequestQueue(
                // Request a string response from the provided URL.
                new JsonObjectRequest(Request.Method.POST, changePasswordURL, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                listener.ResponseSuccess(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Got error");
                                listener.ResponseFailure(error);
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String,String> header = new HashMap<>();
                        header.put("X-ACCESS-TOKEN", AuthenticationManager.getInstance().getAccessToken());
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
                        try {
                            String jsonString = new String(response.data,
                                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));

                            JSONObject result = null;

                            if (jsonString != null && jsonString.length() > 0)
                                result = new JSONObject(jsonString);

                            return Response.success(result,
                                    HttpHeaderParser.parseCacheHeaders(response));
                        } catch (UnsupportedEncodingException e) {
                            return Response.error(new ParseError(e));
                        } catch (JSONException je) {
                            return Response.error(new ParseError(je));
                        }
                    }
                }
        );
    }
}