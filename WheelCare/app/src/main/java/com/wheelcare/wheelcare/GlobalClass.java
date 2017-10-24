package com.wheelcare.wheelcare;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.wheelcare.wheelcare.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Vimal on 10-09-2017.
 */

public class GlobalClass extends Application {

    public ArrayList<ServiceProviderDetails> serviceProviders;

    public static final String TAG = GlobalClass.class.getSimpleName();

    public boolean freezeStatus = false;

    public String userType;

    private static final String SUCCESS = "200";

    public ArrayList<VehicleDetails> pending = null;
    public ArrayList<VehicleDetails> history = null;
    public ArrayList<String> issues = null;
    public ArrayList<UserCarList> userCarLists = null;
    public ArrayList<VehicleDetails> userhistory = null;
    // MARK: URLs

    private static final String ServiceProviderInfoURL = "http://139.59.11.210:8080/wheelcare/rest/consumer/spInfo";
    private static final String SetServiceStatusURL = "http://139.59.11.210:8080/wheelcare/rest/consumer/serviceStatus";
    private static final String FreezeServicesURL = "http://139.59.11.210:8080/wheelcare/rest/consumer/setOpenStatus";

    public GlobalClass() {
        AuthenticationManager.getInstance().globalClass = this;
        pending = new ArrayList<>();
        history = new ArrayList<>();
        issues = new ArrayList<>();
        userCarLists = new ArrayList<>();
        userhistory = new ArrayList<>();
    }

    public String getUserType() {
        SharedPreferences preferences = this.getApplicationContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        this.userType = preferences.getString("userType", null);
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
        SharedPreferences preferences = this.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userType", userType);
        editor.apply();
    }

    public void getIssueList() {
        for (int i = 0; i < 10; i++) {
            String value = "Issue " + (i+1);
            issues.add(value);
        }
    }

    public void arrangePendingAndHistoryList(JSONArray jsonArray) {
        ArrayList<VehicleDetails> services = new ArrayList<>();
        pending.clear();
        history.clear();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject obj = jsonArray.getJSONObject(i);
                VehicleDetails Details = new VehicleDetails();

                Details.vehicleRegistrationNumber = (String) obj.get("reg_no");
                long date = Long.valueOf(obj.getString("slot"));
                Details.date_slot = new Date(date);
                Details.code = (String) obj.get("code");
                Details.customername = (String) obj.get("user_name");
                //Details.serviceStatus = ServiceStatus.NOT_VERIFIED;
                Details.serviceStatus = (ServiceStatus) obj.get("service_status");

                Details.serviceRequired = new ArrayList<>();
                String service_required = obj.getString("service_type");
                if(service_required == "wheel alignment") {
                    Details.serviceRequired.add(ServiceType.WHEEL_ALIGNMENT);
                }
                if(service_required == "wheel balancing") {
                    Details.serviceRequired.add(ServiceType.WHEEL_BALANCING);
                }
                if(service_required == "balancing alignment") {
                    Details.serviceRequired.add(ServiceType.WHEEL_ALIGNMENT);
                    Details.serviceRequired.add(ServiceType.WHEEL_BALANCING);
                }
                Details.vehicleImage = BitmapFactory.decodeResource(getResources(), R.drawable.alto);
                Details.issue = (String) obj.get("issue");
                Details.userID = (String) obj.get("uId");
                Details.comment = (String) obj.get("comment");

                services.add(Details);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (VehicleDetails detail:services) {
            if(detail.serviceStatus == ServiceStatus.DONE || detail.serviceStatus == ServiceStatus.DISMISS) {
                history.add(detail);
            } else {
                pending.add(detail);
            }
        }
    }

    public void getServicesDetails(final PendingServicesListener callback) {
        JSONObject object = createJSONObject();
        if(object != null) {
            getServiceProviderServiceList(callback, object);
        } else {
            Log.e(TAG, "Failed to create JSON object for fetching service provider info");
        }
    }

    public JSONObject createJSONObject() {
        JSONObject object = new JSONObject();
        try {
            object.put("userId", AuthenticationManager.getInstance().getUserID());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return object;
    }

    private void getServiceProviderServiceList(final PendingServicesListener callback, final JSONObject object) {
        // Add the request to the RequestQueue.
        WebServiceManager.getInstance(getApplicationContext()).addToRequestQueue(
                // Request a string response from the provided URL.
                new JsonObjectRequest(Request.Method.POST, ServiceProviderInfoURL, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, response.toString());
                                try {
                                    if (Objects.equals(response.get("statusCode"), SUCCESS)) {
                                        // Actual data received here
                                        JSONArray jsonArray = (JSONArray) response.get("services");
                                        Log.d("JSON Object", jsonArray.toString());
                                        String freeze = response.get("open_status").toString();
                                        freezeStatus = (Boolean)(freeze.trim().equals("freeze"));
                                        arrangePendingAndHistoryList(jsonArray);
                                        if(callback != null) callback.RetrievedServices();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, error.toString());
                                if(callback != null) callback.RetrievingServicesFailed(error);
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
                        Log.d(TAG, "StatusCode: "+String.valueOf(response.statusCode));
                        return super.parseNetworkResponse(response);
                    }
                }
        );
    }

    // MARK: For setting the status

    public void setServicesStatus(VehicleDetails detail) {

        JSONObject object = createStatusJSONObject(detail);
        if(object != null) {
            setServiceStatusCall(object);
        } else {
            Log.e(TAG, "Failed to create JSON object for fetching service provider info");
        }
    }

    public JSONObject createStatusJSONObject(VehicleDetails detail) {
        JSONObject object = new JSONObject();
        try {
            object.put("reg_no", detail.vehicleRegistrationNumber);
            String status = new String();
            switch(detail.serviceStatus) {
                case NOT_VERIFIED: status = "not_verified"; break;
                case VERIFIED: status = "verified"; break;
                case STARTED: status = "started"; break;
                case IN_PROGRESS: status = "inprogress"; break;
                case FINALIZING: status = "finalizing";break;
                case DONE: status = "done"; break;
                case DISMISS: status = "cancelled"; break;
            }
            object.put("service_status", status);
            object.put("issue", detail.issue);
            object.put("comment", detail.comment);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return object;
    }

    private void setServiceStatusCall(final JSONObject object) {
        // Add the request to the RequestQueue.
        WebServiceManager.getInstance(getApplicationContext()).addToRequestQueue(
                // Request a string response from the provided URL.
                new JsonObjectRequest(Request.Method.POST, SetServiceStatusURL, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, response.toString());
                                try {
                                    if (Objects.equals(response.get("statusCode"), SUCCESS)) {
                                        // Actual data received here

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, error.toString());
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
                        Log.d(TAG, "StatusCode: "+String.valueOf(response.statusCode));
                        return super.parseNetworkResponse(response);
                    }
                }
        );
    }

    // MARK: For setting the status

    public void freezeServices(PendingServicesListener listner, boolean freeze) {

        JSONObject object = createFreezeJSONObject(freeze);
        if(object != null) {
            freezeCall(listner, object);
        } else {
            Log.e(TAG, "Failed to create JSON object for fetching service provider info");
        }
    }

    public JSONObject createFreezeJSONObject(boolean freeze) {
        JSONObject object = new JSONObject();
        try {
            object.put("userId", AuthenticationManager.getInstance().getUserID());
            object.put("open_status", freeze ? "freeze" : "unfreeze");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return object;
    }

    private void freezeCall(final PendingServicesListener listener, final JSONObject object) {
        // Add the request to the RequestQueue.
        WebServiceManager.getInstance(getApplicationContext()).addToRequestQueue(
                // Request a string response from the provided URL.
                new JsonObjectRequest(Request.Method.POST, FreezeServicesURL, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, response.toString());
                                try {
                                    if (Objects.equals(response.get("statusCode"), SUCCESS)) {
                                        // Actual data received here
                                        listener.RetrievedServices();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, error.toString());
                                listener.RetrievingServicesFailed(error);
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
                        Log.d(TAG, "StatusCode: "+String.valueOf(response.statusCode));
                        return super.parseNetworkResponse(response);
                    }
                }
        );
    }

}