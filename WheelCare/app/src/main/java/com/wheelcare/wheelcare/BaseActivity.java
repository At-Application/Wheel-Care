package com.wheelcare.wheelcare;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wheelcare.wheelcare.R;

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
                            case "OtpActivity": i = new Intent(getApplicationContext(), OtpActivity.class); break;
                            case "RegistrationActivity": i = new Intent(getApplicationContext(), RegisterationActicvity.class); break;
                            case "CarRegistration": i = new Intent(getApplicationContext(), CarRegistration.class); break;
                            case "UserDashboard": i = new Intent(getApplicationContext(), UserDashboard.class); break;
                        }
                    } else {
                        i = new Intent(getApplicationContext(), MainActivity.class);
                        i.putExtra("user_type", "usr");
                    }
                    startActivity(i);
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
}