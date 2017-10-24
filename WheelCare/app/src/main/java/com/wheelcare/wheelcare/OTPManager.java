package com.wheelcare.wheelcare;

import android.content.Context;
import android.telephony.SmsManager;
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
 * Date: 08-08-2017.
 * Author: Vimal Gohel
 * Company: At Application
 * Description: This framework is designed for At Application to be its default OTP request and verification framework
 */

public class OTPManager {

    // MARK: Enums

    private enum OTPType {
        REQUEST,
        VERIFY
    }

    // MARK: Static variables

    private static final String TAG = OTPManager.class.getSimpleName();

    private static final String SUCCESS = "200";

    // Get the object of SmsManager

    final SmsManager sms = SmsManager.getDefault();

    // MARK: URL variables

    private String OTPRequestURL;

    private String OTPVerifyURL;

    private OTPRequestListener requestCallback;

    private OTPVerificationListener verificationCallback;

    public void setRequestCallback(OTPRequestListener requestCallback) {
        this.requestCallback = requestCallback;
    }

    public void setVerificationCallback(OTPVerificationListener verificationCallback) {
        this.verificationCallback = verificationCallback;
    }

    // MARK: Initialization

    public OTPManager() {

    }

    public void setOTPRequestURL(String OTPRequestURL) {
        this.OTPRequestURL = OTPRequestURL;
    }

    public void setOTPVerifyURL(String OTPVerifyURL) {
        this.OTPVerifyURL = OTPVerifyURL;
    }

    // MARK: Related Web services

    public void requestOTP(Context context, final OTPRequestListener callback) {
        final JSONObject object = new JSONObject();
        try {
            object.put("userId", AuthenticationManager.getInstance().getUserID());
            setRequestCallback(callback);
            OTPService(context, object, OTPType.REQUEST);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void verifyOTP(Context context, final OTPVerificationListener callback, final String code) {
        final JSONObject object = new JSONObject();
        try {
            object.put("userId", AuthenticationManager.getInstance().getUserID());
            object.put("loginOTP", code);
            setVerificationCallback(callback);
            OTPService(context, object, OTPType.VERIFY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void OTPService(Context context, final JSONObject object, final OTPType type) {

        String url;
        if(type == OTPType.REQUEST) {
            url = OTPRequestURL;
        } else {
            url = OTPVerifyURL;
        }

        // Add the request to the RequestQueue.
        WebServiceManager.getInstance(context).addToRequestQueue(
                // Request a string response from the provided URL.
                new JsonObjectRequest(Request.Method.POST, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, response.toString());
                                try {
                                    if (Objects.equals((String) response.get("statusCode"), SUCCESS)) {
                                        Log.d(TAG, "Inside");
                                        if(type == OTPType.REQUEST) {
                                            requestCallback.OTPRequestSuccessful();
                                        } else {
                                            verificationCallback.OTPVerificationSuccessful();
                                        }
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
                                if(type == OTPType.REQUEST) {
                                    requestCallback.OTPRequestFailed(error);
                                } else {
                                    verificationCallback.OTPVerificationFailed(error);
                                }
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
                        return super.parseNetworkResponse(response);
                    }
                }
        );
    }
}