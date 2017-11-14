package com.wheelcare.wheelcare;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.wheelcare.wheelcare.R;

/**
 * Created by Lenovo on 8/6/2017.
 */

public class OtpActivity extends RootActivity implements View.OnClickListener, OTPRequestListener, OTPVerificationListener {
    private static final String TAG = OtpActivity.class.getSimpleName();

    private BroadcastReceiver mReceiver;

    private OTPManager otpManager = new OTPManager();

    IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");


    private static final String otpRequestURL = "http://" + GlobalClass.IPAddress + GlobalClass.Path + "loginOTP";
    private static final String otpVerifyURL = "http://" + GlobalClass.IPAddress + GlobalClass.Path + "loginOTPValidate";

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

    OTPRequestListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        otpManager.setOTPRequestURL(otpRequestURL);
        otpManager.setOTPVerifyURL(otpVerifyURL);
        otpManager.requestOTP(this.getApplicationContext(), this);

        text_auto_detect= (TextView)findViewById(R.id.text_auto_detect);
        progressBar1= (ProgressBar)findViewById(R.id.progressBar1);
        or_border=(TextView)findViewById(R.id.or_border);
        Bundle recieve_number = getIntent().getExtras();

        String mobile_number = recieve_number.getString("mobile_number");

        or_border.setText(mobile_number);
        et_otp= (EditText)findViewById(R.id.et_otp);
        btn_submit= (Button)findViewById(R.id.btn_submit);
        text_resend_sms= (TextView)findViewById(R.id.text_resend_sms);
        text_otp_error= (TextView)findViewById(R.id.text_otp_error);

        listener = this;

        text_resend_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otpManager.requestOTP(getApplicationContext(), listener);
            }
        });

        text_resend_sms.setPaintFlags(text_resend_sms.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

        custom_font_light = Typeface.createFromAsset(getApplicationContext().getAssets(), "serenity-light.ttf");

        text_otp_error.setVisibility(View.INVISIBLE);
        text_auto_detect.setTypeface(custom_font_light);
        or_border.setTypeface(custom_font_light);
        //text_manual_code.setTypeface(custom_font_light);
        et_otp.setTypeface(custom_font_light);
        btn_submit.setTypeface(custom_font_light);
        text_resend_sms.setTypeface(custom_font_light);
        text_otp_error.setTypeface(custom_font_light);

        checkSMSPermission();

        btn_submit.setOnClickListener(this);

        et_otp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

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

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
          final Bundle bundle = intent.getExtras();

                Log.d(TAG, "onReceive");
                // Retrieves a map of extended data from the intent.

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
                                String message = currentMessage.getDisplayMessageBody();

                                message = message.substring(22,26);
                                try
                                {
                                if (senderNum.equals("IM-WHEELC"))
                                {
                                    et_otp.setText(message);
                                    btn_submit.performClick();
                                    btn_submit.setPressed(true);
                                }
                            } catch(Exception e){}
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
            if(((GlobalClass)getApplicationContext()).isInternetAvailable()) {
                otpManager.verifyOTP(this.getApplicationContext(), this, et_otp.getText().toString());
            }
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

   public static final int MY_PERMISSIONS_RECEIVE_SMS = 99;
    public boolean checkSMSPermission(){
         if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECEIVE_SMS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECEIVE_SMS},
                        MY_PERMISSIONS_RECEIVE_SMS);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECEIVE_SMS},
                        MY_PERMISSIONS_RECEIVE_SMS);
            }
            return false;
        } else {
            return true;
        }
    }
}
