package com.wheelcare.wheelcare;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.wheelcare.wheelcare.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Lenovo on 8/6/2017.
 */

public class BaseActivity extends RootActivity implements View.OnClickListener {

    private TextView et_user;
    private TextView et_service;
    private ImageView image_user;
    private ImageView image_service;
    private Typeface custom_font_light;

    private static final String TAG = BaseActivity.class.getSimpleName();

    private static final String CarsURL = "http://" + GlobalClass.IPAddress + GlobalClass.Path + "myCar";

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.d("onPostResume","Activated");
    }

    @Override
    public void onClick(View view) {
        Bundle bundle= new Bundle();
        Intent i = new Intent();

        if(AuthenticationManager.getInstance().isSessionValid() == SessionValidity.VALID) {
            i = new Intent(getApplicationContext(), MainActivity.class);
            switch (view.getId()){
                case R.id.text_user:
                    i.putExtra("user_type", "usr");
                    break;
                case R.id.text_provider:
                    i.putExtra("user_type", "sp");
                    break;
            }
            startActivity(i);
        } else {
            String userType = ((GlobalClass)getApplicationContext()).getUserType();
            switch(view.getId()) {
                case R.id.text_user:
                    if (userType != null && Objects.equals(userType, "usr")) {
                        ((GlobalClass) getApplicationContext()).setUserType("usr");
                        Log.d("ACTIVITY", AuthenticationManager.getInstance().getScreen());
                        switch(AuthenticationManager.getInstance().getScreen()) {
                            case "MainActivity": i = new Intent(getApplicationContext(), MainActivity.class); break;
//                            case "OtpActivity": i = new Intent(getApplicationContext(), OtpActivity.class); break;
                            case "RegistrationActivity": i = new Intent(getApplicationContext(), RegisterationActicvity.class); break;
                            case "CarRegistration": {
                                Log.d("CAR SIZE", String.valueOf(((GlobalClass)getApplicationContext()).vehicles.size()));
                                if(((GlobalClass)getApplicationContext()).vehicles.size() == 0) {
                                    if(((GlobalClass)getApplicationContext()).isInternetAvailable()) {
                                        requestCars("CarRegistration");
                                    }
                                }
                            } break;
                            case "UserDashboard": {
                                Log.d("CAR SIZE", String.valueOf(((GlobalClass)getApplicationContext()).vehicles.size()));
                                if(((GlobalClass)getApplicationContext()).vehicles.size() == 0) {
                                    if(((GlobalClass)getApplicationContext()).isInternetAvailable()) {
                                        requestCars("UserDashboard");
                                    }
                                }
                            } break;
                        }
                    } else {
                        i = new Intent(getApplicationContext(), MainActivity.class);
                        i.putExtra("user_type", "usr");
                        startActivity(i);
                    }
                    break;

                case R.id.text_provider:
                    if(userType != null && Objects.equals(userType, "sp")) {
                        ((GlobalClass)getApplicationContext()).setUserType("sp");
                        i = new Intent(getApplicationContext(), ServiceProviderDashboard.class);
                    } else {
                        i = new Intent(getApplicationContext(), MainActivity.class);
                        i.putExtra("user_type", "sp");
                    }
                    startActivity(i);
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base1);
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        ((GlobalClass)getApplicationContext()).vehicles = new ArrayList<>();
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        ((GlobalClass)getApplicationContext()).isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        //welcome_text= (TextView)findViewById(R.id.welcome_text);
        et_user= (TextView)findViewById(R.id.text_user);
        et_service= (TextView)findViewById(R.id.text_provider);
        //option= (TextView) findViewById(R.id.option);
        //or_border= (TextView)findViewById(R.id.or_border);

        custom_font_light = Typeface.createFromAsset(getApplicationContext().getAssets(), "Calibri.ttf");

       // welcome_text.setTypeface(custom_font_light);
       // or_border.setTypeface(custom_font_light);
        //option.setTypeface(custom_font_light);
        et_user.setTypeface(custom_font_light);
        et_service.setTypeface(custom_font_light);


        et_user.setOnClickListener(this);
        et_service.setOnClickListener(this);

        AuthenticationManager.getInstance().startTokenExpiryCheck();
    }

    public void requestCars(final String activity) {

        final JSONObject object = new JSONObject();
        try {
            object.put("userId", AuthenticationManager.getInstance().getUserID());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(object == null) return;

        WebServiceManager.getInstance(getApplicationContext()).addToRequestQueue(
                // Request a string response from the provided URL.
                new JsonObjectRequest(Request.Method.POST, CarsURL, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                //Log.d(TAG, response.toString());
                                // Actual data received here
                                try {
                                    if(response != null) {
                                        JSONArray array = response.getJSONArray("myCars");
                                        ((GlobalClass) getApplicationContext()).vehicles.clear();
                                        for (int i = 0; i < array.length(); i++) {
                                            ((GlobalClass) getApplicationContext()).vehicles.add(new Vehicle(array.getJSONObject(i)));
                                        }
                                    }
                                    Log.d("CAR SIZE", String.valueOf(((GlobalClass)getApplicationContext()).vehicles.size()));
                                    Intent i = new Intent();
                                    if(Objects.equals(activity, "CarRegistration")) {
                                        i = new Intent(getApplicationContext(), CarRegistration.class);
                                    } else {
                                        i = new Intent(getApplicationContext(), UserDashboard.class);
                                    }
                                    startActivity(i);
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
                        if(response.statusCode == 500) {
                            Toast.makeText(getApplicationContext(), "Server is under maintainance. Please try after sometime.", Toast.LENGTH_LONG).show();
                        }
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