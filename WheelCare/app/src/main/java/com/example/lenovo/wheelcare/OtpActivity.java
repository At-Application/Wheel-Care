package com.example.lenovo.wheelcare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.w3c.dom.Text;

/**
 * Created by Lenovo on 8/6/2017.
 */

public class OtpActivity extends RootActivity implements View.OnClickListener, OTPRequestListener, OTPVerificationListener {
    private static final String TAG = OtpActivity.class.getSimpleName();

    private BroadcastReceiver mReceiver;

    private OTPManager otpManager = new OTPManager();

    private static final String otpRequestURL = "http://139.59.11.210:8080/wheelcare/rest/consumer/loginOTP";
    private static final String otpVerifyURL = "http://139.59.11.210:8080/wheelcare/rest/consumer/loginOTPValidate";

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

        otpManager.setOTPRequestURL(otpRequestURL);
        otpManager.setOTPVerifyURL(otpVerifyURL);

        otpManager.requestOTP(this.getApplicationContext(), this);

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
    protected void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Log.d(TAG, "onReceive");
                // Retrieves a map of extended data from the intent.
                final Bundle bundle = intent.getExtras();

                try {

                    if (bundle != null) {

                        final Object[] pdusObj = (Object[]) bundle.get("pdus");

                        if(pdusObj != null) {
                            for (int i = 0; i < pdusObj.length; i++) {

                                SmsMessage currentMessage;

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    String format = bundle.getString("format");
                                    currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i], format);
                                } else {
                                    currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                                }

                                Log.d(TAG, currentMessage.getDisplayOriginatingAddress());

                                String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                                String senderNum = phoneNumber;
                                String message = currentMessage.getDisplayMessageBody().split(":")[1];

                                message = message.substring(0, message.length() - 1);
                                Log.i("SmsReceiver", "senderNum: " + senderNum + "; message: " + message);

                                Intent myIntent = new Intent("otp");
                                myIntent.putExtra("message", message);
                                // Show Alert

                            } // end for loop
                        }
                    } // bundle is null


                } catch (Exception e) {
                    Log.e("SmsReceiver", "Exception smsReceiver" +e);
                }
            }
        };

        this.registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(mReceiver);
    }

    @Override
    public void onClick(View view) {
        if (isValid){
            otpManager.verifyOTP(this.getApplicationContext(), this, et_otp.getText().toString());
        }
    }

    @Override
    public void OTPRequestSuccessful() {
        // Nothing to be done
    }

    @Override
    public void OTPRequestFailed(VolleyError error) {
        Log.e(TAG, error.toString());
    }

    @Override
    public void OTPVerificationSuccessful() {
        startActivity(new Intent(getApplicationContext(),RegisterationActicvity.class));
    }

    @Override
    public void OTPVerificationFailed(VolleyError error) {
        Log.e(TAG, error.toString());
    }
}
