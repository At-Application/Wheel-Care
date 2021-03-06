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

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.wheelcare.wheelcare.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends RootActivity implements View.OnClickListener, LoginListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private String userType = "usr";

    private static final String CarsURL = "http://" + GlobalClass.IPAddress + GlobalClass.Path + "myCar";

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

    public static final String loginURL = "http://" + GlobalClass.IPAddress + GlobalClass.Path + "mobileLoginAuth";
    public static final String renewURL = "http://" + GlobalClass.IPAddress + GlobalClass.Path + "getRefreshToken";
    public static final String forgotPasswordURL = "http://" + GlobalClass.IPAddress + GlobalClass.Path + "forgotPassword";

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
        if ((isValidPassword)&&(isValidMobile)) {
            if (((GlobalClass) getApplicationContext()).isInternetAvailable()) {
                login();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please enter the details correctly", Toast.LENGTH_SHORT).show();
        }
    }

    public void setupAuthentication() {
        AuthenticationManager.getInstance().setAuthenticationType(AuthenticationType.OAUTH);
        AuthenticationManager.getInstance().setLoginURL(loginURL);
        AuthenticationManager.getInstance().setRenewURL(renewURL);
        AuthenticationManager.getInstance().setForgotPasswordURL(forgotPasswordURL);
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
                if (((GlobalClass) getApplicationContext()).isInternetAvailable()) {
                    requestCars(response);
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
        Toast.makeText(getApplicationContext(), "Failed to Log in", Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(this, ForgotPassword.class).putExtra("user_type", userType);
        startActivity(intent);
    }

    void launchActivity(JSONObject response) {
        try {
            if (Objects.equals((String) response.get("statusDesc"), "user not registered")) {
                Log.i(TAG, "User not registered");
                AuthenticationManager.getInstance().setMainScreen("RegistrationActivity");
                startActivity(new Intent(getApplicationContext(), RegisterationActicvity.class));
            } else if (Objects.equals((String) response.get("statusDesc"), "car not registered")) {
                Log.i(TAG, "Car not registered");
                AuthenticationManager.getInstance().setMainScreen("CarRegistration");
                startActivity(new Intent(getApplicationContext(), CarRegistration.class));
            } else {
                Log.i(TAG, "Login Successful");
                Bundle send_number = new Bundle();
                send_number.putString("mobile_number", edit_mobile.getText().toString());
                startActivity(new Intent(getApplicationContext(), OtpActivity.class).putExtras(send_number));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void requestCars(final JSONObject activity) {

        JSONObject object = null;

        try {
            if (!Objects.equals((String) activity.get("statusDesc"), "account already exist")) {
                launchActivity(activity);
            } else {
                Log.i(TAG, "Account already Exists");
                Toast.makeText(getApplicationContext(), "User already registered", Toast.LENGTH_LONG).show();
                object = new JSONObject();
                object.put("userId", AuthenticationManager.getInstance().getUserID());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(object == null) return;

        final JSONObject finalObject = object;
        WebServiceManager.getInstance(getApplicationContext()).addToRequestQueue(
                // Request a string response from the provided URL.
                new JsonObjectRequest(Request.Method.POST, CarsURL, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                //Log.d(TAG, response.toString());
                                // Actual data received here
                                try {
                                    if(response != null) {
                                        JSONArray array = response.getJSONArray("myCars");
                                        ((GlobalClass) getApplicationContext()).vehicles.clear();
                                        for (int i = 0; i < array.length(); i++) {
                                            ((GlobalClass) getApplicationContext()).vehicles.add(new Vehicle(array.getJSONObject(i)));
                                        }
                                    }
                                    AuthenticationManager.getInstance().setMainScreen("UserDashboard");
                                    startActivity(new Intent(getApplicationContext(), UserDashboard.class));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, error.toString());
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> header = new HashMap<>();
                        header.put("X-ACCESS-TOKEN", AuthenticationManager.getInstance().getAccessToken());
                        Log.e(TAG, "header " + header);
                        return header;
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() {
                        try {
                            return finalObject == null ? null : finalObject.toString().getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", finalObject.toString(), "utf-8");
                            return null;
                        }
                    }

                    @Override
                    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                        Log.d(TAG, "StatusCode: " + String.valueOf(response.statusCode));
                        try {
                            String jsonString = new String(response.data,
                                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));

                            JSONObject result = null;

                            if (jsonString != null && jsonString.length() > 0)
                                result = new JSONObject(jsonString);

                            return Response.success(result,
                                    HttpHeaderParser.parseCacheHeaders(response));
                        } catch (UnsupportedEncodingException e) {
                            return Response.error(new ParseError(e));
                        } catch (JSONException je) {
                            return Response.error(new ParseError(je));
                        }
                    }
                }
        );
    }
}