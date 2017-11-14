package com.wheelcare.wheelcare;

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

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

/**
 * Created by Vimal on 06-11-2017.
 */

public class ForgotPassword extends RootActivity implements ForgotPasswordListener {

    EditText emailID;

    Boolean isValidEmail = false;

    private static final String TAG = ForgotPassword.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forgot_password);

        emailID = (EditText) findViewById(R.id.emailId_text);

        emailID.addTextChangedListener(new TextWatcher(){

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (emailID.getText().toString().matches("^ ") ){
                    isValidEmail= false;
                }else if(emailID.getText().toString().matches("^[a-zA-Z0-9._-]+@[a-z]+.[a-z]+$")){
                    isValidEmail = true;
                }else {
                    isValidEmail = false;
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable arg0) {}
        });

        Button button = (Button) findViewById(R.id.submit_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((GlobalClass)getApplicationContext()).isInternetAvailable()) {
                    if(isValidEmail) {
                        submit();
                    } else {
                        if(emailID.getText().toString().length() == 0) {
                            Toast.makeText(getApplicationContext(), "Field cannot be left empty", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Invalid Email address", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    private void submit() {
        final JSONObject object = new JSONObject();
        try {
            Bundle extra = getIntent().getExtras();
            if(extra != null) {
                object.put("usertype", extra.getString("user_type"));
                object.put("emailId", emailID.getText().toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(object == null) return;

        AuthenticationManager.getInstance().forgotPassword(this, this, object);
    }

    @Override
    public void ResponseSuccess(JSONObject object) {
        Toast.makeText(this, "Please check your email address", Toast.LENGTH_LONG).show();
        this.finish();
    }

    @Override
    public void ResponseFailure(VolleyError error) {
        if(error.networkResponse.statusCode == 404) {
            Toast.makeText(this, "Email address is not registered", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Request for Forgot password failed", Toast.LENGTH_LONG).show();
        }
    }
}
