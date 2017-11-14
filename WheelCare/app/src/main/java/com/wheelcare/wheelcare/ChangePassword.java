package com.wheelcare.wheelcare;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Created by Vimal on 06-11-2017.
 */

public class ChangePassword extends RootActivity implements ChangePasswordListener {

    EditText oldPassword, newPassword, confirmPassword;
    TextView notMatch;
    Typeface calibri;

    public static final String changePasswordURL = "http://" + GlobalClass.IPAddress + GlobalClass.Path + "changePassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AuthenticationManager.getInstance().setChangePasswordURL(changePasswordURL);

        setContentView(R.layout.activity_change_password);
        calibri = Typeface.createFromAsset(getApplicationContext().getAssets(), "Calibri.ttf");

        Button button = (Button) findViewById(R.id.submit_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((GlobalClass)getApplicationContext()).isInternetAvailable()) {
                    submit();
                }
            }
        });

        oldPassword = (EditText) findViewById(R.id.OldPassword);
        newPassword = (EditText) findViewById(R.id.NewPassword);
        confirmPassword = (EditText) findViewById(R.id.ConfirmPassword);

        oldPassword.setTypeface(calibri);
        newPassword.setTypeface(calibri);
        confirmPassword.setTypeface(calibri);

        notMatch = (TextView) findViewById(R.id.NotMatch);
        notMatch.setVisibility(View.INVISIBLE);

        oldPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        newPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(!Objects.equals(newPassword.getText().toString(), confirmPassword.getText().toString())) {
                    notMatch.setVisibility(View.VISIBLE);
                } else {
                    notMatch.setVisibility(View.INVISIBLE);
                }
            }
        });

        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(!Objects.equals(newPassword.getText().toString(), confirmPassword.getText().toString())) {
                    notMatch.setVisibility(View.VISIBLE);
                } else {
                    notMatch.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void submit() {
        if(!(oldPassword.getText().toString().length() >= 8 && newPassword.getText().toString().length() >= 8 && confirmPassword.getText().toString().length() >= 8)) {
            Toast.makeText(this, "Password should be minimum 8 characters", Toast.LENGTH_LONG).show();
        } else if(Objects.equals(newPassword.getText().toString(), confirmPassword.getText().toString())) {

            final JSONObject object = new JSONObject();
            try {
                object.put("userId", AuthenticationManager.getInstance().getUserID());
                object.put("currpwd", oldPassword.getText().toString());
                object.put("newpwd", newPassword.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (object == null) return;


            AuthenticationManager.getInstance().changePassword(this, this, object);
        }
    }

    @Override
    public void ResponseSuccess(JSONObject object) {
        Toast.makeText(this, "Password changed successfully", Toast.LENGTH_LONG).show();
        this.finish();
    }

    @Override
    public void ResponseFailure(VolleyError error) {
        switch(error.networkResponse.statusCode) {
            case 423: Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_LONG).show(); break;

            default:
            Toast.makeText(this, "Please check your password", Toast.LENGTH_LONG).show();
        }
    }
}
