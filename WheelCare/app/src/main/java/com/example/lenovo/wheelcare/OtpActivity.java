package com.example.lenovo.wheelcare;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Lenovo on 8/6/2017.
 */

public class OtpActivity extends RootActivity implements View.OnClickListener {
    private Typeface custom_font_light;
    private TextView text_auto_detect;
    private ProgressBar progressBar1;

    private TextView or_border;
    private TextView text_manual_code;
    private EditText et_otp;
    private Button btn_submit;
    private TextView text_resend_sms;
    protected TextView text_otp_error;
    private boolean isValid;
    private TextView txt_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        txt_title= (TextView)findViewById(R.id.txt_title);
        text_auto_detect= (TextView)findViewById(R.id.text_auto_detect);
        progressBar1= (ProgressBar)findViewById(R.id.progressBar1);
        or_border=(TextView)findViewById(R.id.or_border);
        text_manual_code=(TextView)findViewById(R.id.text_manual_code);
        et_otp= (EditText)findViewById(R.id.et_otp);
        btn_submit= (Button)findViewById(R.id.btn_submit);
        text_resend_sms= (TextView)findViewById(R.id.text_resend_sms);
        text_otp_error= (TextView)findViewById(R.id.text_otp_error);

        text_resend_sms.setPaintFlags(text_resend_sms.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

        custom_font_light = Typeface.createFromAsset(getApplicationContext().getAssets(), "serenity-light.ttf");

        text_otp_error.setVisibility(View.INVISIBLE);
        txt_title.setTypeface(custom_font_light);
        text_auto_detect.setTypeface(custom_font_light);
        or_border.setTypeface(custom_font_light);
        text_manual_code.setTypeface(custom_font_light);
        et_otp.setTypeface(custom_font_light);
        btn_submit.setTypeface(custom_font_light);
        text_resend_sms.setTypeface(custom_font_light);
        text_otp_error.setTypeface(custom_font_light);

        btn_submit.setOnClickListener(this);

        et_otp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                et_otp.setBackgroundColor(Color.parseColor("#ffffff"));
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                text_otp_error.setVisibility(View.INVISIBLE);
                if((et_otp.getText().toString().matches("^[0-9]{4}$"))){
                    Log.d("valid otp", "onTextChanged: ");
                    text_otp_error.setVisibility(View.INVISIBLE);
                     isValid = true;
                }else if (et_otp.getText().toString().matches("^ ")){
                    text_otp_error.setVisibility(View.VISIBLE);
                    isValid= false;
                }else{
                    text_otp_error.setVisibility(View.VISIBLE);
                    isValid= false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        if (isValid){
            startActivity(new Intent(getApplicationContext(),RegisterationActicvity.class));
        }

    }

}
