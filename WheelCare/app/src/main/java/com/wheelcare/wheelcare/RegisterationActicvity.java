package com.wheelcare.wheelcare;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.wheelcare.wheelcare.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Lenovo on 8/5/2017.
 */

public class RegisterationActicvity extends RootActivity implements View.OnClickListener, RegistrationListener {

    private static final String TAG = RegisterationActicvity.class.getSimpleName();

    public static final String registrationURL = "http://" + GlobalClass.IPAddress + GlobalClass.Path + "doRegistration";

    private TextView welcome_text;
    private EditText et_user;
    private EditText et_referral;
    private TextView text_email_error;
    private boolean isValidFullName = false;
    private boolean isValidEmail = false;
   // private EditText et_fullname;
    private TextView text_fullname_error;
    private EditText et_fullname;
    private boolean isValidRefferral = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        AuthenticationManager.getInstance().setMainScreen("RegistrationActivity");
        setupRegistrationURL();

        welcome_text= (TextView)findViewById(R.id.welcome_text);
        et_user = (EditText) findViewById(R.id.et_email);
        et_referral = (EditText) findViewById(R.id.et_referal);
        et_fullname= (EditText)findViewById(R.id.et_fullname);

        Button btn_submit = (Button) findViewById(R.id.btn_submit);

        text_email_error = (TextView) findViewById(R.id.text_email_error);
        text_fullname_error= (TextView)findViewById(R.id.text_fullname_error);

        final TextView text_referal_error = (TextView) findViewById(R.id.text_referal_error);

        text_referal_error.setVisibility(View.INVISIBLE);

        Typeface custom_font_light = Typeface.createFromAsset(getApplicationContext().getAssets(), "Calibri.ttf");


        welcome_text.setTypeface(custom_font_light);
        et_user.setTypeface(custom_font_light);
        text_referal_error.setTypeface(custom_font_light);
        text_fullname_error.setTypeface(custom_font_light);
        et_fullname.setTypeface(custom_font_light);
        et_referral.setTypeface(custom_font_light);
        btn_submit.setTypeface(custom_font_light);
        text_email_error.setTypeface(custom_font_light);
        text_email_error.setVisibility(View.INVISIBLE);
        text_fullname_error.setVisibility(View.INVISIBLE);
        btn_submit.setOnClickListener(this);


        et_fullname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isValidFullName= false;
                text_fullname_error.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(et_fullname.getText().toString().matches("^ ")){
                    text_fullname_error.setVisibility(View.VISIBLE);
                    isValidFullName = false;
                }else{
                    isValidFullName= true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        et_user.addTextChangedListener(new TextWatcher(){
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                text_email_error.setVisibility(View.INVISIBLE);
                if (et_user.getText().toString().matches("^ ") ){
                    Log.d("Match","invalid");
                    text_email_error.setText("This field cannot be left empty");
                    // et_user.setText("");
                    text_email_error.setVisibility(View.VISIBLE);
                    isValidEmail= false;
                }else if(et_user.getText().toString().matches("^[a-zA-Z0-9._-]+@[a-z]+.[a-z]+$")){

                    text_email_error.setVisibility(View.INVISIBLE);
                    isValidEmail = true;
                }else {
                    text_email_error.setVisibility(View.VISIBLE);
                    isValidEmail = false;
                }

            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){

                text_email_error.setVisibility(View.INVISIBLE);
                text_email_error.setText("Invalid Email ID");

            }
            @Override
            public void afterTextChanged(Editable arg0) {

            }
        });
        et_referral.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                text_referal_error.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_referral.getText().toString().matches("^ ") ){
                    Log.d("Match","invalid");
                    isValidRefferral = false;
                } else if(et_referral.getText().toString().length() < 8){

                    isValidRefferral = false;
                }else{
                    isValidRefferral = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View view) {
       // Bundle bundle= new Bundle();
       if (et_fullname.getText().toString().length()==0){
           text_fullname_error.setVisibility(View.VISIBLE);
           isValidFullName = false;
       }else if(et_user.getText().toString().length()==0){
           text_email_error.setVisibility(View.VISIBLE);
            isValidEmail = false;
       }
        if ((isValidFullName)&&(isValidEmail)){
            if(((GlobalClass)getApplicationContext()).isInternetAvailable()) {
                register();
            }
            //bundle.putString("Mobile",et_user.getText().toString());
           //startActivity(new Intent(getApplicationContext(),OtpActivity.class).putExtras(bundle));
        }
    }

    public void register() {
        JSONObject object = createRegisterJSONObject();
        if(object != null) {
            AuthenticationManager.getInstance().register(this.getApplicationContext(), this, object);
        } else {
            Log.e(TAG, "Failed to create JSON object");
        }
    }

    public JSONObject createRegisterJSONObject() {
        JSONObject object = new JSONObject();
        try {
            object.put("userId", AuthenticationManager.getInstance().getUserID());
            object.put("username", et_fullname.getText().toString());
            object.put("emailId", et_user.getText().toString());
            if(et_referral.getText() != null) {
                object.put("referralCode", et_referral.getText().toString());
            } else {
                object.put("referralCode", "");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return object;
    }

    void setupRegistrationURL() {
        AuthenticationManager.getInstance().setRegisterURL(registrationURL);
    }

    @Override
    public void registrationSuccessful() {
        Log.i(TAG, "Registration Successful");
        //Toast.makeText(this.getApplicationContext(), "Registration Successful", Toast.LENGTH_LONG).show();
        startActivity(new Intent(getApplicationContext(),CarRegistration.class));
    }

    @Override
    public void registrationFailed(VolleyError error) {
        Log.e(TAG, error.toString());
        if(error.networkResponse.statusCode == 409) {
            Toast.makeText(this.getApplicationContext(), "Email already registered", Toast.LENGTH_LONG).show();
        }
    }
}
