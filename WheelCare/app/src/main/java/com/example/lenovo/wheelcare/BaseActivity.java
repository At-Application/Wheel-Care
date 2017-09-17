package com.example.lenovo.wheelcare;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
        switch (view.getId()){
            case R.id.text_user:
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                i.putExtra("user_type", "usr");
                startActivity(i);
                break;
            case R.id.text_provider:
                Intent j = new Intent(getApplicationContext(),MainActivity.class);
                j.putExtra("user_type", "sp");
                startActivity(j);
                break;
            default:
                 break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base1);
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
    }
}