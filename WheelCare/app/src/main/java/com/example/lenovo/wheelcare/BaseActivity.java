package com.example.lenovo.wheelcare;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Lenovo on 8/6/2017.
 */

public class BaseActivity extends RootActivity implements View.OnClickListener {

    private TextView welcome_text;
    private EditText et_user;
    private EditText et_service;
    private ImageView image_user;
    private ImageView image_service;
    private Typeface custom_font_light;

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.d("onPostResume","Activated");
        et_user.setBackgroundColor(Color.parseColor("#ffffff"));
        et_service.setBackgroundColor(Color.parseColor("#ffffff"));
    }

    @Override
    public void onClick(View view) {
        Bundle bundle= new Bundle();
        switch (view.getId()){
            case R.id.et_user:
                et_user.setBackgroundColor(Color.parseColor("#00C2F3"));
                bundle.putInt("user_id",1);
                startActivity(new Intent(getApplicationContext(),MainActivity.class).putExtras(bundle));
                break;
            case R.id.et_service:
                et_service.setBackgroundColor(Color.parseColor("#00C2F3"));
                bundle.putInt("service_id",2);
                startActivity(new Intent(getApplicationContext(),MainActivity.class).putExtras(bundle));
                break;
            default:
                 break;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        welcome_text= (TextView)findViewById(R.id.welcome_text);
        et_user= (EditText)findViewById(R.id.et_user);
        et_service= (EditText)findViewById(R.id.et_service);

        custom_font_light = Typeface.createFromAsset(getApplicationContext().getAssets(), "serenity-light.ttf");

        welcome_text.setTypeface(custom_font_light);
        et_user.setTypeface(custom_font_light);
        et_service.setTypeface(custom_font_light);


        et_user.setOnClickListener(this);
        et_service.setOnClickListener(this);
    }
}
