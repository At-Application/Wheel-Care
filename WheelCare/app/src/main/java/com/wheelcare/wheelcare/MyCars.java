package com.wheelcare.wheelcare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.wheelcare.wheelcare.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyCars extends Fragment {

    ListView listView;
    View view;
    CustomListViewAdapter adapter;

    private static final String URL = "http://" + GlobalClass.IPAddress + GlobalClass.Path + "myCar";
    private static final String deleteURL = "http://" + GlobalClass.IPAddress + GlobalClass.Path + "rmCar";
    private static final String TAG = MyCars.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        view = inflater.inflate(R.layout.my_cars, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = (ListView) view.findViewById(R.id.list);
        adapter = new CustomListViewAdapter();
        listView.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.plus);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity().getApplicationContext(), CarRegistration.class);
                i.putExtra("MyCars", true);
                startActivity(i);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if(((GlobalClass)getActivity().getApplicationContext()).vehicles.size() == 0) {
            if(((GlobalClass)getActivity().getApplicationContext()).isInternetAvailable()) {
                requestCars();
            }
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    public class CustomListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if(((GlobalClass)getActivity().getApplicationContext()).vehicles.size() > 0)
            return ((GlobalClass)getActivity().getApplicationContext()).vehicles.size();
            else
                return 1;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if(((GlobalClass)getActivity().getApplicationContext()).vehicles.size() > 0) {


                LayoutInflater mInflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

                convertView = mInflater.inflate(R.layout.mycars_list_item, null);

                convertView.findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(getActivity().getApplicationContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();
                        deleteCar(position);
                    }
                });

                TextView txtDesc = (TextView) convertView.findViewById(R.id.registrationnumber);
                TextView txtTitle = (TextView) convertView.findViewById(R.id.carvariant);
                ImageView imageView = (ImageView) convertView.findViewById(R.id.carimage);

                txtDesc.setText(((GlobalClass) getActivity().getApplicationContext()).vehicles.get(position).registration_number);
                txtTitle.setText(((GlobalClass) getActivity().getApplicationContext()).vehicles.get(position).type);
                Bitmap bmp = BitmapFactory.decodeByteArray(((GlobalClass) getActivity().getApplicationContext()).vehicles.get(position).image, 0, ((GlobalClass) getActivity().getApplicationContext()).vehicles.get(position).image.length);
                imageView.setImageBitmap(bmp);
            } else {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.no_cars, null);
                convertView.setMinimumHeight(parent.getMeasuredHeight());
                ConstraintLayout layout = (ConstraintLayout)convertView.findViewById(R.id.backLayout);
                layout.setMinHeight(parent.getMeasuredHeight());
            }

            return convertView;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
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
                new JsonObjectRequest(Request.Method.POST, URL, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, response.toString());
                                // Actual data received here
                                try {
                                    JSONArray array = response.getJSONArray("myCars");
                                    ((GlobalClass)getActivity().getApplicationContext()).vehicles.clear();
                                    for(int i = 0; i < array.length(); i++) {
                                        ((GlobalClass)getActivity().getApplicationContext()).vehicles.add(new Vehicle(array.getJSONObject(i)));
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

    public void deleteCar(final int position) {

        if(!(((GlobalClass)getActivity().getApplicationContext()).isInternetAvailable())) {
            return;
        }

        final JSONObject object = new JSONObject();
        try {
            object.put("userId", AuthenticationManager.getInstance().getUserID());
            object.put("reg_no", ((GlobalClass)getActivity().getApplicationContext()).vehicles.get(position).registration_number);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(object == null) return;

        WebServiceManager.getInstance(getActivity().getApplicationContext()).addToRequestQueue(
                // Request a string response from the provided URL.
                new JsonObjectRequest(Request.Method.POST, deleteURL, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
//                                Log.d(TAG, response.toString());
                                // Actual data received here
                                ((GlobalClass)getActivity().getApplicationContext()).vehicles.remove(position);
                                adapter.notifyDataSetChanged();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                switch(error.networkResponse.statusCode) {
                                    case 405:
                                        Toast.makeText(getActivity().getApplicationContext(), "This vehicle has one free service left", Toast.LENGTH_LONG).show();
                                        break;

                                    case 409:
                                        Toast.makeText(getActivity().getApplicationContext(), "Vehicle assigned for service. Cannot be deleted", Toast.LENGTH_LONG).show();
                                        break;
                                }
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