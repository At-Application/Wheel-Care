package com.example.lenovo.wheelcare;


import android.content.Intent;
import android.content.pm.ProviderInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Lenovo on 8/5/2017.
 */

public class RegisterationActicvity extends RootActivity implements View.OnClickListener {
    private EditText et_user;
    private EditText et_referral;
    private TextView text_email_error;
    private boolean isValidFullName = false;
    private boolean isValidEmail = false;
   // private EditText et_fullname;
    private TextView text_fullname_error;
    private EditText et_fullname;
    private TextView txt_title;
    private boolean isValidRefferral = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        txt_title = (TextView)findViewById(R.id.txt_title);
        et_user = (EditText) findViewById(R.id.et_email);
        et_referral = (EditText) findViewById(R.id.et_referal);
        et_fullname= (EditText)findViewById(R.id.et_fullname);

        Button btn_submit = (Button) findViewById(R.id.btn_submit);

        text_email_error = (TextView) findViewById(R.id.text_email_error);
        text_fullname_error= (TextView)findViewById(R.id.text_fullname_error);


        TextView text_header_email = (TextView) findViewById(R.id.text_header_email);
        TextView text_header_referral = (TextView)findViewById(R.id.text_header_referral);
        final TextView text_header_fullName = (TextView) findViewById(R.id.text_header_fullname);

        //final TextView et_fullName = (TextView) findViewById(R.id.et_fullname);
        TextView reqdField = (TextView) findViewById(R.id.reqd_field);
        TextView text_referal_error = (TextView) findViewById(R.id.text_referal_error);

        text_referal_error.setVisibility(View.INVISIBLE);

        Typeface custom_font_light = Typeface.createFromAsset(getApplicationContext().getAssets(), "serenity-light.ttf");


        txt_title.setTypeface(custom_font_light);
        et_user.setTypeface(custom_font_light);
        text_header_referral.setTypeface(custom_font_light);
        text_referal_error.setTypeface(custom_font_light);
        text_fullname_error.setTypeface(custom_font_light);
        et_fullname.setTypeface(custom_font_light);
        et_referral.setTypeface(custom_font_light);
        btn_submit.setTypeface(custom_font_light);
        text_header_email.setTypeface(custom_font_light);
        text_email_error.setTypeface(custom_font_light);
        text_header_fullName.setTypeface(custom_font_light);
        reqdField.setTypeface(custom_font_light);
        text_email_error.setVisibility(View.INVISIBLE);
        text_fullname_error.setVisibility(View.INVISIBLE);
        btn_submit.setOnClickListener(this);


        et_fullname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                et_fullname.setBackgroundColor(Color.parseColor("#ffffff"));
                isValidFullName= false;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                text_header_fullName.setVisibility(View.INVISIBLE);
                if(et_fullname.getText().toString().matches("^ ")){
                    text_fullname_error.setVisibility(View.VISIBLE);
                    isValidFullName = false;
                }else{
                    isValidFullName= true;
                    text_header_fullName.setVisibility(View.INVISIBLE);
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
                et_user.setBackgroundColor(Color.parseColor("#ffffff"));
                if (et_user.getText().toString().matches("^ ") ){
                    Log.d("Match","invalid");
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            @Override
            public void afterTextChanged(Editable arg0) {

            }
        });
        et_referral.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //text_password_error.setVisibility(View.INVISIBLE);
                et_referral.setBackgroundColor(Color.parseColor("#ffffff"));

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_referral.getText().toString().matches("^ ") ){
                    Log.d("Match","invalid");
                    // et_user.setText("");
                    //text_password_error.setVisibility(View.VISIBLE);
                    isValidRefferral = false;
                }else if(et_referral.getText().toString().length()<8){
                    //text_password_error.setVisibility(View.VISIBLE);
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
       }
        if ((isValidFullName)&&(isValidEmail)){
            startActivity(new Intent(getApplicationContext(),CarRegistration.class));
            //bundle.putString("Mobile",et_user.getText().toString());
           //startActivity(new Intent(getApplicationContext(),OtpActivity.class).putExtras(bundle));
        }
    }
}
