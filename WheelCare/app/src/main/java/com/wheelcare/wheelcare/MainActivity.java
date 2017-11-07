package com.wheelcare.wheelcare;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.wheelcare.wheelcare.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class MainActivity extends RootActivity implements View.OnClickListener, LoginListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private String userType = "usr";

    private EditText edit_mobile;
    private EditText edit_userpass;
    private Typeface calibri;
    private TextView text_invalid_password;
    private boolean  isValidMobile;
    private boolean isValidPassword;
    private TextView text_mobile_error;
    private TextView text_password_error;
    boolean is_hidden = true;
    private TextView header;

    public static final String loginURL = "http://" + GlobalClass.IPAddress + "/wheelcare/rest/consumer/mobileLoginAuth";
    public static final String renewURL = "http://" + GlobalClass.IPAddress + "/wheelcare/rest/consumer/getRefreshToken";
    public static final String forgotPasswordURL = "http://" + GlobalClass.IPAddress + "/wheelcare/rest/consumer/forgotPassword";
    public static final String changePasswordURL = "http://" + GlobalClass.IPAddress + "/wheelcare/rest/consumer/changePassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);

        Bundle extra = getIntent().getExtras();
        if(extra != null) {
            userType = extra.getString("user_type");
        }

        setupAuthentication();

        checkInternetPermission();

        edit_mobile =(EditText)findViewById(R.id.edit_mobile);
        edit_userpass =(EditText)findViewById(R.id.edit_userpass);

        calibri = Typeface.createFromAsset(getApplicationContext().getAssets(), "Calibri.ttf");

        header= (TextView)findViewById(R.id.header) ;

        Button btn_submit = (Button) findViewById(R.id.btn_submit);
        final ImageView chech_box = (ImageView) findViewById(R.id.chech_box);
        Button forgot_password = (Button) findViewById(R.id.text_forgot_password);
        text_mobile_error= (TextView)findViewById(R.id.text_mobile_error);
        text_invalid_password = (TextView)findViewById(R.id.text_invalid_password);
        text_password_error= (TextView)findViewById(R.id.text_password_error);
        forgot_password.setPaintFlags(forgot_password.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        //text_password_error.setPaintFlags(text_password_error.getPaintFlags()|Paint.UNDERLINE_TEXT_FLAG);
        //text_mobile_error.setPaintFlags(text_mobile_error.getPaintFlags()|Paint.UNDERLINE_TEXT_FLAG);

        text_mobile_error.setVisibility(View.INVISIBLE);
        text_password_error.setVisibility(View.INVISIBLE);
        btn_submit.setOnClickListener(this);
        text_invalid_password.setVisibility(View.INVISIBLE);

        edit_mobile.requestFocus();
        header.setTypeface(calibri);
        edit_mobile.setTypeface(calibri);
        text_password_error.setTypeface(calibri);
        text_mobile_error.setTypeface(calibri);
        edit_userpass.setTypeface(calibri);
        btn_submit.setTypeface(calibri);
        text_invalid_password.setTypeface(calibri);
        forgot_password.setTypeface(calibri);

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendForgotPasswordRequest();
            }
        });

        /**************************** Password Show  ****************************/

        chech_box.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                if (is_hidden) {
                    edit_userpass.setInputType(129);
                    edit_userpass.setTypeface(calibri);
                    is_hidden = false;
                    Drawable res = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        res = getResources().getDrawable(R.drawable.password_hide, null);
                    } else {
                        res = getResources().getDrawable(R.drawable.password_hide);
                    }
                    chech_box.setImageDrawable(res);
                } else {
                    edit_userpass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    edit_userpass.setTypeface(calibri);
                    is_hidden= true;
                    Drawable res = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        res = getResources().getDrawable(R.drawable.password_show, null);
                    } else {
                        res = getResources().getDrawable(R.drawable.password_show);
                    }
                    chech_box.setImageDrawable(res);
                }
            }
        });

        /*******************************************************************************/

        edit_mobile.addTextChangedListener(new TextWatcher(){
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (edit_mobile.getText().toString().matches("^ ") ){
                    Log.d("match", "onTextChanged: ");
                    text_mobile_error.setVisibility(View.VISIBLE);
                    isValidMobile= false;
                }else if(edit_mobile.getText().toString().length()==10){
                    text_mobile_error.setVisibility(View.INVISIBLE);
                    isValidMobile= true;
                }else{
                    isValidMobile=false;
                }
                    //edit_username.setText("");
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){
                Log.d("match", "beforeTextChanged: ");
                text_mobile_error.setVisibility(View.VISIBLE);
                isValidMobile= false;
            }
            @Override
            public void afterTextChanged(Editable arg0) {

            }
        });

        edit_userpass.addTextChangedListener(new TextWatcher(){

            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                //Log.d("Text changed to:", (String) s);
                Log.d("MSG","text changed");
                if((edit_userpass.getText().toString().matches("^[A-Za-z0-9][A-Za-z0-9@#%&*]*$"))&&(edit_userpass.getText().toString().length()>=8)){
                    text_password_error.setVisibility(View.INVISIBLE);
                    isValidPassword= true;
                }else if(edit_userpass.getText().toString().matches("^ ")){
                    text_password_error.setText("Should be atleast 8 characters long");
                    text_password_error.setVisibility(View.VISIBLE);
                    isValidPassword= false;
               } else{
                    //text_invalid_password.setText("Should have atleast one alphabet, number and special character");
                    text_password_error.setVisibility(View.VISIBLE);
                    isValidPassword=false;
               }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after){
                //edit_mobile.setBackgroundColor(Color.parseColor("#ffffff"));
               // edit_userpass.setBackgroundResource(R.drawable.custom_edit_box);
            }

            @Override
            public void afterTextChanged(Editable arg0) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // MARK: Login Related Functions

    @Override
    public void onClick(View v) {
        //if ((isValidPassword)&&(isValidMobile)) {
            login();
        //}
    }

    public void setupAuthentication() {
        AuthenticationManager.getInstance().setAuthenticationType(AuthenticationType.OAUTH);
        AuthenticationManager.getInstance().setLoginURL(loginURL);
        AuthenticationManager.getInstance().setRenewURL(renewURL);
        AuthenticationManager.getInstance().setForgotPasswordURL(forgotPasswordURL);
        AuthenticationManager.getInstance().setChangePasswordURL(changePasswordURL);
    }

    public void login() {
        JSONObject object = createJSONObject();
        if(object != null) {
            AuthenticationManager.getInstance().login(this.getApplicationContext(), this, object);
        } else {
            Log.e(TAG, "Failed to create JSON object");
        }
    }

    public JSONObject createJSONObject() {
        JSONObject object = new JSONObject();
        try {
            object.put("usertype", userType);
            object.put("mobilenumber", edit_mobile.getText().toString());
            object.put("password", edit_userpass.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return object;
    }

    @Override
    public void loginSuccess(JSONObject response) {
        Log.i(TAG, "Login Success: " + userType);
        try {
            ((GlobalClass)getApplicationContext()).setUserType(userType);
            if(Objects.equals(userType, "usr")) {
                if (Objects.equals((String) response.get("statusDesc"), "account already exist")) {
                    Log.i(TAG, "Account already Exists");
                    Toast.makeText(getApplicationContext(), "User already registered", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), UserDashboard.class));
                } else if(Objects.equals((String) response.get("statusDesc"), "user not registered")) {
                    Log.i(TAG, "User not registered");
                    startActivity(new Intent(getApplicationContext(), RegisterationActicvity.class));
                } else if(Objects.equals((String) response.get("statusDesc"), "car not registered")) {
                    Log.i(TAG, "Car not registered");
                    startActivity(new Intent(getApplicationContext(), CarRegistration.class));
                } else {
                    Log.i(TAG, "Login Successful");
                    Bundle send_number = new Bundle();
                    send_number.putString("mobile_number",edit_mobile.getText().toString());
                    startActivity(new Intent(getApplicationContext(), OtpActivity.class).putExtras(send_number));
                }
            } else {
                if (Objects.equals((String) response.get("statusDesc"), "SP login authentication successful")) {
                    startActivity(new Intent(getApplicationContext(), ServiceProviderDashboard.class));
                }
            }
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void loginFailed(VolleyError error) {
        Log.e(TAG, error.toString());
    }

    public static final int MY_PERMISSIONS_INTERNET = 99;
    public boolean checkInternetPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.INTERNET)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET},
                        MY_PERMISSIONS_INTERNET);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET},
                        MY_PERMISSIONS_INTERNET);
            }
            return false;
        } else {
            return true;
        }
    }

    private void sendForgotPasswordRequest() {
        Intent intent = new Intent(this, ForgotPassword.class);
        startActivity(intent);
    }
}