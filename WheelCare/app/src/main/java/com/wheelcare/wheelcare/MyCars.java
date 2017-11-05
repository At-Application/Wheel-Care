package com.wheelcare.wheelcare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
    ArrayList<Vehicle> rowItems;
    View view;
    CustomListViewAdapter adapter;

    private static final String URL = "http://" + GlobalClass.IPAddress + "/wheelcare/rest/consumer/myCar";
    private static final String deleteURL = "http://" + GlobalClass.IPAddress + "/wheelcare/rest/consumer/rmCar";
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

        rowItems = new ArrayList<>();

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
    public void onResume() {
        super.onResume();
        rowItems = ((GlobalClass)getActivity().getApplicationContext()).getCarList();
        if(rowItems.size() == 0) {
            requestCars();
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    public class CustomListViewAdapter extends BaseSwipeAdapter {

        @Override
        public int getSwipeLayoutResourceId(int pos) {
            return R.id.swipe;
        }

        @Override
        public View generateView(final int position, ViewGroup viewGroup) {
            View view=null;

            LayoutInflater mInflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            view = mInflater.inflate(R.layout.mycars_list_item, null);
                SwipeLayout swipeLayout = (SwipeLayout)view.findViewById(getSwipeLayoutResourceId(position));
                swipeLayout.addSwipeListener(new SimpleSwipeListener() {
                    @Override
                    public void onOpen(SwipeLayout layout) {
                        YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
                    }
                });
            view.findViewById(R.id.trash).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(getActivity().getApplicationContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();
                        deleteCar(position);
                    }
                });

            return view;
        }

        @Override
        public void fillValues(int i, View view) {
            TextView txtDesc = (TextView) view.findViewById(R.id.registrationnumber);
            TextView txtTitle = (TextView) view.findViewById(R.id.carvariant);
            ImageView imageView = (ImageView) view.findViewById(R.id.carimage);

            txtDesc.setText(rowItems.get(i).registration_number);
            txtTitle.setText(rowItems.get(i).type);
            Bitmap bmp = BitmapFactory.decodeByteArray( rowItems.get(i).image, 0, rowItems.get(i).image.length);
            imageView.setImageBitmap(bmp);
        }

        @Override
        public int getCount() {
            if(rowItems.size() > 0)
            return rowItems.size();
            else
                return 0;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
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
                                    rowItems.clear();
                                    for(int i = 0; i < array.length(); i++) {
                                        rowItems.add(new Vehicle(array.getJSONObject(i)));
                                    }
                                    ((GlobalClass)getActivity().getApplicationContext()).saveCarList(rowItems);
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

        final JSONObject object = new JSONObject();
        try {
            object.put("userId", AuthenticationManager.getInstance().getUserID());
            object.put("reg_no", rowItems.get(position).registration_number);
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
                                rowItems.remove(position);
                                ((GlobalClass)getActivity().getApplicationContext()).saveCarList(rowItems);
                                adapter.notifyDataSetChanged();
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