package com.wheelcare.wheelcare;

import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forgot_password);

        emailID = (EditText) findViewById(R.id.emailId_text);

        Button button = (Button) findViewById(R.id.submit_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    private void submit() {
        final JSONObject object = new JSONObject();
        try {
            object.put("emailId", emailID.getText().toString());
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
        Toast.makeText(this, "Request for Forgot password failed", Toast.LENGTH_LONG).show();
    }
}
