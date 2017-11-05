package com.wheelcare.wheelcare;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.graphics.Typeface.BOLD;

/**
 * Created by Vimal on 10-09-2017.
 */

public class UserHistory extends Fragment {

    private Typeface calibri;

    ArrayList<Vehicle> models;

    HistoryAdapter adapter;

    private static final String TAG = UserHistory.class.getSimpleName();
    private static final String CarsURL = "http://" + GlobalClass.IPAddress + "/wheelcare/rest/consumer/myCar";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        calibri = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "Calibri.ttf");
        models = ((GlobalClass)getActivity().getApplicationContext()).getCarList();
        if(models.size() == 0) {
            requestCars();
        } else {
            requestHistory();
        }
        setupListView(view);
    }

    // MARK: List view related functions

    private void setupListView(View view) {
        ListView listView = (ListView) view.findViewById(R.id.user_history_listview);
        adapter = new HistoryAdapter();
        listView.setAdapter(adapter);
    }

    private class HistoryAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            Log.d("SIZE", String.valueOf(((GlobalClass)getActivity().getApplicationContext()).userhistory.size()));

            return ((GlobalClass)getActivity().getApplicationContext()).userhistory.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {

            final UserHistoryDetails service = ((GlobalClass)getActivity().getApplicationContext()).userhistory.get(position);

            if(service.serviceStatus == ServiceStatus.DONE || service.serviceStatus == ServiceStatus.DISMISS) {

                SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy(kk:mm)");
                String date = fmt.format(service.date_slot);

                view = getLayoutInflater(null).inflate(R.layout.user_history_detail_view, null);
                final TextView registrationNumber = (TextView) view.findViewById(R.id.vehiclenumber);
                final TextView username = (TextView) view.findViewById(R.id.username);
                final TextView wheelAlignment = (TextView) view.findViewById(R.id.WheelAlignmentCheckBox);
                final TextView wheelBalancing = (TextView) view.findViewById(R.id.WheelBalancingCheckBox);
                final TextView code = (TextView) view.findViewById(R.id.Code);
                final TextView dateSlot = (TextView) view.findViewById(R.id.date_slot);
                final ImageView vehicleImage = (ImageView) view.findViewById(R.id.Vehicle);
                final LinearLayout statusBar = (LinearLayout) view.findViewById(R.id.StatusBar);
                final TextView historyStatus = (TextView) view.findViewById(R.id.CompletionStatus);

                statusBar.setVisibility(View.INVISIBLE);

                registrationNumber.setTypeface(calibri);
                username.setTypeface(calibri, BOLD);
                wheelAlignment.setTypeface(calibri);
                wheelBalancing.setTypeface(calibri);
                code.setTypeface(calibri, BOLD);
                dateSlot.setTypeface(calibri);
                historyStatus.setTypeface(calibri, BOLD);

                historyStatus.setTextSize(23);
                code.setTextSize(25);

                registrationNumber.setText(service.vehicleRegistrationNumber);

                if(service.serviceRequired.size() == 2) {
                    wheelBalancing.setText("Wheel Balancing");
                    wheelAlignment.setText("Wheel Alignment");
                } else {
                    wheelAlignment.setVisibility(View.INVISIBLE);
                    if (service.serviceRequired.contains(ServiceType.WHEEL_BALANCING)) {
                        wheelBalancing.setHeight(20);
                    } else {
                        wheelBalancing.setText("Wheel Alignment");
                        wheelBalancing.setHeight(20);
                    }
                }

                dateSlot.setText(date);
                code.setText("CODE: ");
                code.append(service.code);

                int pos = 0;
                for(; pos < models.size(); pos++) {
                    if(models.get(pos).id == service.modelId) {
                        break;
                    }
                }

                Bitmap bmp = BitmapFactory.decodeByteArray( models.get(pos).image, 0, models.get(pos).image.length);
                vehicleImage.setImageBitmap(bmp);

                // Main Logic
                if (service.serviceStatus != ServiceStatus.DONE) {
                    historyStatus.setText(service.issue);
                    historyStatus.setBackground(getActivity().getDrawable(R.color.red));
                }
            } else {
                view = null;
            }
            return view;
        }
    }

    public void requestCars() {

        final JSONObject object = new JSONObject();
        try {
            object.put("userId", AuthenticationManager.getInstance().getUserID());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(object == null) return;

        WebServiceManager.getInstance(getActivity().getApplicationContext()).addToRequestQueue(
                // Request a string response from the provided URL.
                new JsonObjectRequest(Request.Method.POST, CarsURL, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, response.toString());
                                // Actual data received here
                                try {
                                    JSONArray array = response.getJSONArray("myCars");
                                    models.clear();
                                    for(int i = 0; i < array.length(); i++) {
                                        models.add(new Vehicle(array.getJSONObject(i)));
                                    }
                                    ((GlobalClass)getActivity().getApplicationContext()).saveCarList(models);
                                    requestHistory();
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

    public void requestHistory() {

        final JSONObject object = new JSONObject();
        try {
            object.put("userId", AuthenticationManager.getInstance().getUserID());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(object == null) return;

        WebServiceManager.getInstance(getActivity().getApplicationContext()).addToRequestQueue(
                // Request a string response from the provided URL.
                new JsonObjectRequest(Request.Method.POST, GlobalClass.UserHistoryURL, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, response.toString());
                                // Actual data received here
                                try {
                                    ((GlobalClass)getActivity().getApplicationContext()).userhistory.clear();
                                    JSONArray jsonArray = (JSONArray) response.get("history");
                                    for(int i = 0; i < jsonArray.length(); i++) {
                                        ((GlobalClass)getActivity().getApplicationContext()).userhistory.add(new UserHistoryDetails(jsonArray.getJSONObject(i)));
                                    }
                                    adapter.notifyDataSetChanged();
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