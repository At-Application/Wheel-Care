package com.example.lenovo.wheelcare;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by Lenovo on 8/5/2017.
 */

public class RegisterationActicvity extends RootActivity implements View.OnClickListener {
    private EditText et_user;
    private EditText et_pass;
    private EditText et_repass;
    private EditText et_email;
    private Spinner spinner_gender;
    private EditText et_age;
    private CheckBox show_password;
    private TextView text_username_error;
    private TextView text_password_error;
    private TextView text_renter_password_error;
    private Typeface custom_font_bold,custom_font_light;
    private TextView text_email_error;
    private TextView text_header_username;
    private TextView txt_password_header;
    private TextView txt_re_enter_password_error;
    private TextView txt_re_enterpass;
    private TextView txt_head_email;
    private TextView txt_head_sex;
    private TextView txt_head_age;
    private boolean isValid = false;
    private Button btn_submit;
    private TextView text_header_fullName;
    private TextView et_fullName;
    private TextView reqdField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        et_user = (EditText) findViewById(R.id.et_uname);
        et_pass = (EditText) findViewById(R.id.et_pass);
        et_repass = (EditText) findViewById(R.id.et_repass);
        btn_submit=(Button)findViewById(R.id.btn_submit);
        show_password =(CheckBox) findViewById(R.id.checkBox_showpassword);
        text_username_error = (TextView) findViewById(R.id.text_username_error);
        text_password_error = (TextView) findViewById(R.id.text_password_error);
        text_renter_password_error = (TextView) findViewById(R.id.text_renter_password_error);
        text_header_username = (TextView) findViewById(R.id.text_header_username);
        text_header_fullName= (TextView)findViewById(R.id.text_header_fullname);
        txt_password_header = (TextView) findViewById(R.id.txt_password_header);
        txt_re_enterpass = (TextView) findViewById(R.id.txt_re_enterpass);
        et_fullName = (TextView)findViewById(R.id.et_fullname);
        reqdField = (TextView)findViewById(R.id.reqd_field);

        custom_font_light = Typeface.createFromAsset(getApplicationContext().getAssets(), "serenity-light.ttf");
        custom_font_bold = Typeface.createFromAsset(getApplicationContext().getAssets(), "serenity-bold.ttf");

        et_user.setTypeface(custom_font_light);
        et_pass.setTypeface(custom_font_light);
        show_password.setTypeface(custom_font_light);
        et_repass.setTypeface(custom_font_light);
        btn_submit.setTypeface(custom_font_light);
        text_password_error.setTypeface(custom_font_light);
        text_header_username.setTypeface(custom_font_light);
        txt_password_header.setTypeface(custom_font_light);
        text_username_error.setTypeface(custom_font_light);
        txt_re_enterpass.setTypeface(custom_font_light);
        text_renter_password_error.setTypeface(custom_font_light);
        text_header_fullName.setTypeface(custom_font_light);
        et_fullName.setTypeface(custom_font_light);
        reqdField.setTypeface(custom_font_light);







        text_username_error.setVisibility(View.INVISIBLE);
        text_password_error.setVisibility(View.INVISIBLE);
        text_renter_password_error.setVisibility(View.INVISIBLE);
        btn_submit.setOnClickListener(this);


        show_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    et_pass.setInputType(129);
                    et_repass.setInputType(129);


                } else {
                    et_pass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    et_repass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

                }
            }
        });
        et_user.addTextChangedListener(new TextWatcher(){
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                text_username_error.setVisibility(View.INVISIBLE);
                et_user.setBackgroundColor(Color.parseColor("#ffffff"));
                if (et_user.getText().toString().matches("^ ") ){
                    Log.d("Match","invalid");
                    // et_user.setText("");
                    text_username_error.setVisibility(View.VISIBLE);
                    isValid= false;
                }else if(et_user.getText().toString().length()<8){
                    text_username_error.setVisibility(View.VISIBLE);
                    isValid = false;
                }else {
                    isValid= true;
                }

            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            @Override
            public void afterTextChanged(Editable arg0) {

            }
        });
        et_pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                text_password_error.setVisibility(View.INVISIBLE);
                et_pass.setBackgroundColor(Color.parseColor("#ffffff"));

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_pass.getText().toString().matches("^ ") ){
                    Log.d("Match","invalid");
                    // et_user.setText("");
                    text_password_error.setVisibility(View.VISIBLE);
                    isValid= false;
                }else if(et_pass.getText().toString().length()<8){
                    text_password_error.setVisibility(View.VISIBLE);
                    isValid = false;
                }else{
                    isValid = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_repass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                text_renter_password_error.setVisibility(View.INVISIBLE);
                et_repass.setBackgroundColor(Color.parseColor("#ffffff"));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                text_renter_password_error.setVisibility(View.VISIBLE);
                if (et_repass.getText().toString().equals(et_pass.getText().toString()) ){
                    Log.d("Match","invalid");
                    // et_user.setText("");
                    text_renter_password_error.setVisibility(View.INVISIBLE);
                    isValid= true;
                }else{
                    isValid = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        if (isValid){
           // startActivity(new Intent(getApplicationContext(),OtpActivity.class));
        }
    }
}
