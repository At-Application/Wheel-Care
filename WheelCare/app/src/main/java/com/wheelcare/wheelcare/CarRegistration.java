package com.wheelcare.wheelcare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.BoringLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.wheelcare.wheelcare.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Lenovo on 8/9/2017.
 */

public class CarRegistration extends BaseActivity implements View.OnClickListener {
    private Typeface custom_font_light;
    private ArrayAdapter<CharSequence> adapter;
    public Spinner brand_spinner, model_spinner, type_spinner;
    private ArrayList<String> model_items;
    private ArrayList<String> manufaturer_items;
    private ArrayList<Integer> model_id;

    byte[] byteArray;
    private ImageView carImage;
    private TextView txt_title;
    private TextView text_invalid_regno;
    private EditText et_carRegno;
    // private TextView text_register_number;
    private boolean isValidCarNumber = false;
    private Button submit_btn;

    private static final String TAG = UserHome.class.getSimpleName();

    private static final String CarRegistrationURL = "http://" + GlobalClass.IPAddress + "/wheelcare/rest/consumer/carRegistration";
    private static final String URL = "http://" + GlobalClass.IPAddress + "/wheelcare/rest/consumer/carInfo";
    private static final String ImageURL = "http://" + GlobalClass.IPAddress + "/wheelcare/rest/consumer/carImg";

    private static final String SUCCESS = "200";

    private ArrayList<Vehicle> list;

    JSONArray array;

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (isValidCarNumber) {
            registerCar();
        } else if (et_carRegno.getText().toString().length() == 0) {
            text_invalid_regno.setText("All field are required");
            text_invalid_regno.setVisibility(View.VISIBLE);
        }
    }

    // private String[] car_models= new String[5];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_registration);

        list = ((GlobalClass)getApplicationContext()).getCarList();

        manufaturer_items = new ArrayList<>();
        model_items = new ArrayList<>();
        model_id = new ArrayList<>();
        manufaturer_items.add("Manufacturer");
        model_items.add("Model");
        model_id.add(0);

        //txt_title= (TextView)findViewById(R.id.txt_title);
        brand_spinner = (Spinner) findViewById(R.id.brand_spinner);
        model_spinner = (Spinner) findViewById(R.id.model_spinner);
        //model_spinner.setClickable(false);

        type_spinner = (Spinner) findViewById(R.id.type_spinner);
        //type_spinner.setClickable(false);
        //text_register_number = (TextView)findViewById(R.id.text_Register_number);
        text_invalid_regno = (TextView) findViewById(R.id.text_invalid_regno);
        // spinner_text= (TextView)findViewById(R.id.spinner_text);
        carImage = (ImageView) findViewById(R.id.carImage);
        et_carRegno = (EditText) findViewById(R.id.et_carRegno);
        et_carRegno.setEnabled(false);
        submit_btn = (Button) findViewById(R.id.btn_submit);
        custom_font_light = Typeface.createFromAsset(getApplicationContext().getAssets(), "Calibri.ttf");

        //text_register_number.setTypeface(custom_font_light);

        submit_btn.setOnClickListener(this);

        /*txt_title.setText("Wheelcare Car Registration");
        txt_title.setTypeface(custom_font_light);
        */
        text_invalid_regno.setVisibility(View.GONE);

        changeTypeFaceSpinner1();
        changeTypeFaceSpinner2();
        changeTypeFaceSpinner3();

        getVehicleInformation();

        et_carRegno.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                text_invalid_regno.setText("Invalid Registration number");
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (et_carRegno.getText().toString().matches("^ ")) {
                    text_invalid_regno.setVisibility(View.VISIBLE);
                    isValidCarNumber = false;
                } else if (et_carRegno.getText().toString().length() == 10) {
                    text_invalid_regno.setVisibility(View.INVISIBLE);
                    isValidCarNumber = true;
                } else {
                    isValidCarNumber = false;
                }
                //edit_username.setText("");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        brand_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
               /* Toast.makeText(adapterView.getContext(),
                        "OnItemSelectedListener : " + adapterView.getItemAtPosition(pos).toString(),
                        Toast.LENGTH_LONG).show();*/
                changeTypeFaceSpinner2();
                TextView selectedText = (TextView) adapterView.getChildAt(0);
                if (selectedText != null && selectedText.getText() != "Manufacturer") {
                    selectedText.setTextColor(Color.BLACK);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        model_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
               /* Toast.makeText(adapterView.getContext(),
                        "OnItemSelectedListener : " + adapterView.getItemAtPosition(pos).toString(),
                        Toast.LENGTH_LONG).show();*/
                changeTypeFaceSpinner3();
                TextView selectedText = (TextView) adapterView.getChildAt(0);
                if (selectedText != null && selectedText.getText() != "Model") {
                    selectedText.setTextColor(Color.BLACK);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
               /* Toast.makeText(adapterView.getContext(),
                        "OnItemSelectedListener : " + adapterView.getItemAtPosition(pos).toString(),
                        Toast.LENGTH_LONG).show();*/
                TextView selectedText = (TextView) adapterView.getChildAt(0);
                if (selectedText != null && selectedText.getText() != "Engine Type") {
                    selectedText.setTextColor(Color.BLACK);
                    et_carRegno.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void changeTypeFaceSpinner1() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_text, manufaturer_items) {

            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                if (position == 0) {
                    model_spinner.setEnabled(false);
                    type_spinner.setEnabled(false);
                } else {
                    model_spinner.setEnabled(true);
                    populateModels(manufaturer_items.get(position));
                }

                ((TextView) v).setTypeface(custom_font_light);

                return v;
            }

            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {

                    return true;
                }
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                Log.d("positon", String.valueOf(position));
                ((TextView) v).setTypeface(custom_font_light);
                if (position == 0) {
                    // Set the hint text color gray
                    ((TextView) v).setTextColor(Color.GRAY);
                } else {
                    Log.d("position_select", String.valueOf(position));
                    ((TextView) v).setTextColor(Color.BLACK);
                }

                return v;
            }
        };

        //type_spinner.setClickable(false);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brand_spinner.setAdapter(adapter);
    }

    public void changeTypeFaceSpinner2() {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_text, model_items) {

            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                type_spinner.setEnabled(true);

                if (position == 0) {
                    type_spinner.setEnabled(false);
                    carImage.setVisibility(View.VISIBLE);
                    carImage.setImageResource(R.drawable.dummy_car);
                } else {
                    carImage.setVisibility(View.VISIBLE);
                    getVehicleImage(position);
                }
                ((TextView) v).setTypeface(custom_font_light);
                return v;
            }

            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTypeface(custom_font_light);
                if (position == 0) {
                    // Set the hint text color gray
                    ((TextView) v).setTextColor(Color.GRAY);
                } else {

                    ((TextView) v).setTextColor(Color.BLACK);
                }

                return v;
            }
        };
        //model_spinner.setEnabled(true);
        //model_spinner.setClickable(true);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        model_spinner.setAdapter(adapter);
    }

    public void changeTypeFaceSpinner3() {
        final String[] items = new String[4];
        items[0] = "Engine Type";
        items[1] = "Diesel";
        items[2] = "Petrol";
        items[3] = "CNG";
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_text, items) {

            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTypeface(custom_font_light);

                if (position == 0) {

                }
                return v;
            }

            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTypeface(custom_font_light);

                if (position == 0) {
                    // Set the hint text color gray
                    ((TextView) v).setTextColor(Color.GRAY);
                } else {

                    ((TextView) v).setTextColor(Color.BLACK);
                }
                return v;
            }
        };
        //type_spinner.setEnabled(true);
        //type_spinner.setClickable(true);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_spinner.setAdapter(adapter);
    }

    private void startCarRegistration() {
        Toast.makeText(getApplicationContext(), "Car added successfully!", Toast.LENGTH_LONG).show();
        ((GlobalClass)getApplicationContext()).saveCarList(list);
        Bundle extra = getIntent().getExtras();
        Boolean fromMycars = false;
        if (extra != null) {
            fromMycars = extra.getBoolean("MyCars");
        }
        if (fromMycars) this.finish();
        else startActivity(new Intent(getApplicationContext(), UserDashboard.class));
    }

    // MARK: For registering CAR

    public void registerCar() {
        JSONObject object = createJSONObject();
        if (object != null) {
            ServiceProviderCall(object);
        } else {
            Log.e(TAG, "Failed to create JSON object for fetching service provider info");
        }
    }

    public JSONObject createJSONObject() {
        JSONObject object = new JSONObject();
        try {
            object.put("userId", AuthenticationManager.getInstance().getUserID());
            object.put("carManufacture", brand_spinner.getSelectedItem().toString());
            object.put("regNo", et_carRegno.getText());
            object.put("carName", model_spinner.getSelectedItem().toString());
            object.put("carType", type_spinner.getSelectedItem().toString());
            //object.put("carId", model_id.get(model_items.indexOf(model_spinner.getSelectedItem().toString())));
            Vehicle car = new Vehicle();
            car.id = model_id.get(model_items.indexOf(model_spinner.getSelectedItem().toString()));
            car.manufacturer = brand_spinner.getSelectedItem().toString();
            car.registration_number = et_carRegno.getText().toString();
            car.type = type_spinner.getSelectedItem().toString();
            car.image = carImage;
            list.add(car);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return object;
    }

    private void ServiceProviderCall(final JSONObject object) {
        // Add the request to the RequestQueue.
        WebServiceManager.getInstance(getApplicationContext()).addToRequestQueue(
                // Request a string response from the provided URL.
                new JsonObjectRequest(Request.Method.POST, CarRegistrationURL, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, response.toString());
                                // Actual data received here
                                startCarRegistration();
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

    // MARK: For vehicle info

    void populateManufacturers() {
        try {
            for (int i =0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                manufaturer_items.add(object.getString("manufacturer_name"));
            }
            populateModels(manufaturer_items.get(0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void populateModels(String model) {
        try {
            for (int i =0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                if(Objects.equals(obj.getString("manufacturer_name"), model)) {
                    JSONArray arr = obj.getJSONArray("models");
                    model_items.clear();
                    model_id.clear();
                    model_items.add("Model");
                    model_id.add(0);
                    for(int j = 0; j < arr.length(); j++) {
                        JSONObject jsonObject = arr.getJSONObject(j);
                        model_id.add(jsonObject.getInt("model_id"));
                        model_items.add(jsonObject.getString("car_model"));
                    }
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getVehicleInformation() {

        WebServiceManager.getInstance(getApplicationContext()).addToRequestQueue(
                // Request a string response from the provided URL.
                new JsonObjectRequest(Request.Method.GET, URL, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, response.toString());
                                // Actual data received here
                                try {
                                    array = response.getJSONArray("carInfos");
                                    populateManufacturers();
                                    changeTypeFaceSpinner1();
                                    changeTypeFaceSpinner2();
                                    changeTypeFaceSpinner3();
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
                    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                        Log.d(TAG, "StatusCode: " + String.valueOf(response.statusCode));
                        return super.parseNetworkResponse(response);
                    }
                }
        );
    }

    public void getVehicleImage(int pos) {

        final JSONObject object = new JSONObject();
        try {
            object.put("model_id", model_id.get(pos));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(object == null) return;

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
                                    byteArray = Base64.decode(byteString, Base64.DEFAULT);
                                    Bitmap bmp = BitmapFactory.decodeByteArray( byteArray, 0, byteArray.length);
                                    carImage.setImageBitmap(bmp);
                                } catch (JSONException e) {
                                    byteArray = null;
                                    carImage.setImageResource(R.drawable.dummy_car);
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, error.toString());
                                byteArray = null;
                                carImage.setImageResource(R.drawable.dummy_car);
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