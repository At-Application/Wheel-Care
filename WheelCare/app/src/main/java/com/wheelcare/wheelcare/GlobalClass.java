package com.wheelcare.wheelcare;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Vimal on 10-09-2017.
 */

public class GlobalClass extends Application {

    public ArrayList<ServiceProviderDetails> serviceProviders;

    public static final String TAG = GlobalClass.class.getSimpleName();

    public boolean freezeStatus = false;

    public String userType;

    public long endTime = 0;

    public static final String IPAddress = "139.59.77.103:8080";

    private static final String SUCCESS = "200";

    private Timer timer;

    public PendingServicesListener listener;

    private TimerTask timerTask;

    public boolean isConnected;

    public ArrayList<VehicleDetails> pending = null;
    public ArrayList<VehicleDetails> history = null;
    public ArrayList<Issues> issues = null;
    public ArrayList<UserCarList> userCarLists = null;
    public ArrayList<UserHistoryDetails> userhistory = null;
    public ArrayList<Vehicle> vehicles = null;
    // MARK: URLs

    private static final String ServiceProviderInfoURL = "http://" + IPAddress + "/wheelcare/rest/consumer/spInfo";
    private static final String SetServiceStatusURL = "http://" + IPAddress + "/wheelcare/rest/consumer/serviceStatus";
    private static final String FreezeServicesURL = "http://" + IPAddress + "/wheelcare/rest/consumer/setOpenStatus";
    private static final String TempFreezeServicesURL = "http://" + IPAddress + "/wheelcare/rest/consumer/tempFreeze";
    public static final String UserHistoryURL = "http://" + IPAddress + "/wheelcare/rest/consumer/getUserHistory";
    private static final String ImageURL = "http://" + GlobalClass.IPAddress + "/wheelcare/rest/consumer/carImg";

    public GlobalClass() {
        AuthenticationManager.getInstance().globalClass = this;
        pending = new ArrayList<>();
        history = new ArrayList<>();
        issues = new ArrayList<>();
        userCarLists = new ArrayList<>();
        userhistory = new ArrayList<>();
    }

    public boolean isInternetAvailable() {
        if(!isConnected) {
            Toast.makeText(this, "Please connect to internet", Toast.LENGTH_LONG).show();
        }
        return isConnected;
    }

    public void startTempFreezeTimer() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                listener.RetrievedServices();
                timer = null;
            }
        };

        if (endTime > 0 && timer == null) {
            Long current = new Date().getTime();
            timer = new Timer();
            timer.schedule(timerTask, endTime - current);
        }
    }

    public void stopTempFreezeTimer() {
        if(timer != null) timer.cancel();
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
            Issues value = new Issues();
            value.issue = "Issue " + (i+1);
            value.status = false;
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

                String serviceStatusString = (String) obj.get("service_status");
                switch(serviceStatusString) {
                    case "not_verified": Details.serviceStatus = ServiceStatus.NOT_VERIFIED; break;
                    case "verified": Details.serviceStatus = ServiceStatus.VERIFIED; break;
                    case "started": Details.serviceStatus = ServiceStatus.STARTED; break;
                    case "inprogress": Details.serviceStatus = ServiceStatus.IN_PROGRESS; break;
                    case "finalizing": Details.serviceStatus = ServiceStatus.FINALIZING; break;
                    case "done": Details.serviceStatus = ServiceStatus.DONE; break;
                    case "cancelled": Details.serviceStatus = ServiceStatus.DISMISS; break;
                }

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
                Details.model_id = Integer.parseInt(obj.getString("model_id"));
                Details.issue = obj.isNull("issue") ? "" : (String) obj.get("issue");
                Details.userID = (String) obj.get("uId");
                Details.comment = obj.isNull("comment") ? "" : (String) obj.get("comment");

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
                                        endTime = Long.parseLong(response.getString("temp_freeze_end"));
                                        freezeStatus = (Boolean)(freeze.trim().equals("freeze"));
                                        arrangePendingAndHistoryList(jsonArray);
                                        getVehicleImages();
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

    // MARK: For setting the status

    public void tempFreezeServices(PendingServicesListener listner, boolean freeze) {

        JSONObject object = createTempFreezeJSONObject(freeze);
        if(object != null) {
            tempFreezeCall(listner, object);
        } else {
            Log.e(TAG, "Failed to create JSON object for fetching service provider info");
        }
    }

    public JSONObject createTempFreezeJSONObject(boolean freeze) {
        JSONObject object = new JSONObject();
        try {
            long time = 0;
            object.put("spId", AuthenticationManager.getInstance().getUserID());
            if(freeze) {
                time = new Date().getTime();
                Log.d("Start Time", String.valueOf(time));
                object.put("freeze_time_start", String.valueOf(time));
                time = time + (1000 * 60 * 30);
                Log.d("End Time", String.valueOf(time));
                object.put("freeze_time_end", String.valueOf(time));
            } else {
                object.put("freeze_time_start", String.valueOf(time));
                object.put("freeze_time_end", String.valueOf(time));
            }
            endTime = time;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return object;
    }

    private void tempFreezeCall(final PendingServicesListener listener, final JSONObject object) {
        // Add the request to the RequestQueue.
        WebServiceManager.getInstance(getApplicationContext()).addToRequestQueue(
                // Request a string response from the provided URL.
                new JsonObjectRequest(Request.Method.POST, TempFreezeServicesURL, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Actual data received here
                                listener.RetrievedServices();
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

    public ArrayList<Vehicle> getCarList() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("CarList", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("list", "");
        Vehicle[] vlist = gson.fromJson(json, Vehicle[].class);
        try {
            Log.d(TAG, "list = " + vlist[0].model);
            List<Vehicle> vehiclelist = Arrays.asList(vlist);
            return new ArrayList<Vehicle>(vehiclelist);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void saveCarList(ArrayList<Vehicle> list) {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("CarList", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson =  new Gson();
        String json = gson.toJson(list);
        editor.putString("list", json);
        editor.apply();
    }

    public void getVehicleImages() {

        if(vehicles == null) {
            vehicles = new ArrayList<>();
        }

        for(int i = 0, j; i < pending.size(); i++) {
            for(j = 0; j < vehicles.size(); j++) {
                if(pending.get(i).model_id == vehicles.get(j).id) break;
            }
            if(j == (vehicles.size() - 1)) {
                Vehicle v = new Vehicle();
                v.id = pending.get(i).model_id;
            }
        }

        for(int i = 0, j; i < history.size(); i++) {
            for(j = 0; j < vehicles.size(); j++) {
                if(pending.get(i).model_id == vehicles.get(j).id) break;
            }
            if(j == (vehicles.size() - 1)) {
                Vehicle v = new Vehicle();
                v.id = pending.get(i).model_id;
                v.image = null;
                vehicles.add(v);
            }
        }

        for(int i = 0; i < vehicles.size(); i++) {
            if(vehicles.get(i).image != null) continue;

            final JSONObject object = new JSONObject();
            try {
                object.put("model_id", vehicles.get(i).id);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (object == null) return;

            final int finalI = i;
            WebServiceManager.getInstance(getApplicationContext()).addToRequestQueue(
                    // Request a string response from the provided URL.
                    new JsonObjectRequest(Request.Method.POST, ImageURL, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d(TAG, response.toString());
                                    // Actual data received here
                                    try {
                                        String byteString = response.getString("img");
                                        vehicles.get(finalI).image = Base64.decode(byteString, Base64.DEFAULT);
                                        listener.RetrievedServices();
                                    } catch (JSONException e) {
                                        vehicles.get(finalI).image = null;
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d(TAG, error.toString());
                                    vehicles.get(finalI).image = null;
                                }
                            }
                    ) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> header = new HashMap<>();
                            header.put("X-ACCESS-TOKEN", AuthenticationManager.getInstance().getAccessToken());
                            Log.e(TAG, "header " + header);
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
                            Log.d(TAG, "StatusCode: " + String.valueOf(response.statusCode));
                            return super.parseNetworkResponse(response);
                        }
                    }
            );
        }
    }
}