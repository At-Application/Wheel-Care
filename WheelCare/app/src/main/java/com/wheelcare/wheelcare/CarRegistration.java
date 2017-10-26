package com.wheelcare.wheelcare;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.wheelcare.wheelcare.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Lenovo on 8/9/2017.
 */

public class CarRegistration extends BaseActivity implements View.OnClickListener {
    private Typeface custom_font_light;
    private ArrayAdapter<CharSequence> adapter;
    public Spinner brand_spinner,model_spinner,type_spinner;
    private final String [] model_items = new String[2];
    private ImageView carImage;
    private TextView txt_title;
    private TextView text_invalid_regno;
    private EditText et_carRegno;
   // private TextView text_register_number;
    private boolean isValidCarNumber= false;
    private Button submit_btn;

    private static final String TAG = UserHome.class.getSimpleName();

    private static final String CarRegistrationURL = "http://" + GlobalClass.IPAddress + "/wheelcare/rest/consumer/carRegistration";

    private static final String SUCCESS = "200";

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (isValidCarNumber){
            registerCar();
        }else if(et_carRegno.getText().toString().length()==0){
            text_invalid_regno.setText("All field are required");
            text_invalid_regno.setVisibility(View.VISIBLE);
        }
    }

    // private String[] car_models= new String[5];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_registration);

        //txt_title= (TextView)findViewById(R.id.txt_title);
        brand_spinner = (Spinner) findViewById(R.id.brand_spinner);
        model_spinner= (Spinner)findViewById(R.id.model_spinner);
        //model_spinner.setClickable(false);

        type_spinner = (Spinner)findViewById(R.id.type_spinner);
        //type_spinner.setClickable(false);
        //text_register_number = (TextView)findViewById(R.id.text_Register_number);
        text_invalid_regno= (TextView)findViewById(R.id.text_invalid_regno);
       // spinner_text= (TextView)findViewById(R.id.spinner_text);
        carImage = (ImageView)findViewById(R.id.carImage);
        et_carRegno= (EditText)findViewById(R.id.et_carRegno);
        et_carRegno.setEnabled(false);
        submit_btn= (Button)findViewById(R.id.btn_submit);
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
                if (selectedText != null && selectedText.getText() != "Vehicle") {
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
                if (selectedText != null && selectedText.getText() != "Vehicle Type") {
                    selectedText.setTextColor(Color.BLACK);
                    et_carRegno.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void changeTypeFaceSpinner2(){


        //carImage.setImageResource(R.drawable.temp_logo);
        model_items[0]="Vehicle";

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_text, model_items) {

            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                type_spinner.setEnabled(true);
                if (Objects.equals(model_items[position], "Brezza")){
                    carImage.setVisibility(View.VISIBLE);
                    carImage.setImageResource(R.drawable.marutibrezza);
                }else if (Objects.equals(model_items[position], "Bolero")){
                    carImage.setVisibility(View.VISIBLE);
                    carImage.setImageResource(R.drawable.mahindrabolero);
                }else if(Objects.equals(model_items[position], "KWID")){
                    carImage.setVisibility(View.VISIBLE);
                    carImage.setImageResource(R.drawable.renaultkwid);
                }else{
                    type_spinner.setEnabled(false);
                    carImage.setVisibility(View.VISIBLE);
                    carImage.setImageResource(R.drawable.dummy_car);
                }
                ((TextView) v).setTypeface(custom_font_light);
                return v;
            }
            @Override
            public boolean isEnabled(int position) {
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }

            public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                View v =super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTypeface(custom_font_light);
                if(position == 0){
                    // Set the hint text color gray
                    ((TextView)v).setTextColor(Color.GRAY);
                }
                else {

                    ((TextView)v).setTextColor(Color.BLACK);
                }

                return v;
            }
        };
        //model_spinner.setEnabled(true);
        //model_spinner.setClickable(true);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        model_spinner.setAdapter(adapter);
    }

    public void changeTypeFaceSpinner3(){
        final String [] items = new String[4];
        items[0]="Vehicle Type";
        items[1]="Diesel";
        items[2]="Petrol";
        items[3]="CNG";
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_text, items) {

            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTypeface(custom_font_light);

                if(position == 0){

                }
                return v;
            }
            @Override
            public boolean isEnabled(int position) {
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }

            public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                View v =super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTypeface(custom_font_light);

                if(position == 0){
                    // Set the hint text color gray
                    ((TextView)v).setTextColor(Color.GRAY);
                }
                else {

                    ((TextView)v).setTextColor(Color.BLACK);
                }
                return v;
            }
        };
        //type_spinner.setEnabled(true);
        //type_spinner.setClickable(true);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_spinner.setAdapter(adapter);
    }
    public void changeTypeFaceSpinner1(){
        final String [] items = new String[4];
        items[0]="Manufacturer";
        items[1]="Maruti";
        items[2]="Mahindra";
        items[3]="Renault";
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_text, items) {

            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                if (position == 0) {
                    //Log.d("position :", String.valueOf(position));
                    //car_models[1]="Brezza";
                    model_items[1] = "";
                    model_spinner.setEnabled(false);
                    type_spinner.setEnabled(false);
                } else {
                    model_spinner.setEnabled(true);
                    if (position == 1) {
                        model_items[1] = "Brezza";
                    } else if (position == 2) {
                        model_items[1] = "Bolero";
                    } else if (position == 3) {
                        model_items[1] = "KWID";
                    }
                }

                ((TextView) v).setTypeface(custom_font_light);

                return v;
            }

            @Override
            public boolean isEnabled(int position) {
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {

                    return true;
                }
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v =super.getDropDownView(position, convertView, parent);
                Log.d("positon", String.valueOf(position));
                ((TextView) v).setTypeface(custom_font_light);
                if(position == 0){
                    // Set the hint text color gray
                    ((TextView)v).setTextColor(Color.GRAY);
                }
                else {
                    Log.d("position_select", String.valueOf(position));
                    ((TextView)v).setTextColor(Color.BLACK);
                }

                return v;
            }
        };

        //type_spinner.setClickable(false);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brand_spinner.setAdapter(adapter);
    }

    private void startCarRegistration() {
        Toast.makeText(getApplicationContext(), "Car added successfully!", Toast.LENGTH_LONG).show();
        startActivity(new Intent(getApplicationContext(), UserDashboard.class));
    }

    // MARK: For registering CAR

    public void registerCar() {
        JSONObject object = createJSONObject();
        if(object != null) {
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
