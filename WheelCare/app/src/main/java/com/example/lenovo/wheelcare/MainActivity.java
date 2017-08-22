package com.example.lenovo.wheelcare;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Objects;

public class MainActivity extends RootActivity implements View.OnClickListener, LoginListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private String userType = "usr";

    private EditText edit_mobile;
    private EditText edit_userpass;
    private Typeface custom_font_light;
    private TextView text_invalid_password;
    private boolean  isValidMobile;
    private boolean isValidPassword;
    private TextView text_mobile_error;
    private TextView text_password_error;
    private TextView txt_title;
    boolean is_hidden = true;

    public static final String loginURL = "http://139.59.11.210:8080/wheelcare/rest/consumer/mobileLoginAuth";
    public static final String renewURL = "http://139.59.11.210:8080/wheelcare/rest/consumer/getRefreshToken";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extra = getIntent().getExtras();
        if(extra != null) {
            userType = extra.getString("user_type");
        }

        setupAuthentication();

        edit_mobile =(EditText)findViewById(R.id.edit_mobile);
        edit_userpass =(EditText)findViewById(R.id.edit_userpass);

        custom_font_light = Typeface.createFromAsset(getApplicationContext().getAssets(), "serenity-light.ttf");

        txt_title=(TextView)findViewById(R.id.txt_title);
        Button btn_submit = (Button) findViewById(R.id.btn_submit);
        ImageView chech_box = (ImageView) findViewById(R.id.chech_box);
        TextView forgot_password = (TextView) findViewById(R.id.text_forgot_password);
        text_mobile_error= (TextView)findViewById(R.id.text_mobile_error);
        text_invalid_password = (TextView)findViewById(R.id.text_invalid_password);
        text_password_error= (TextView)findViewById(R.id.text_password_error);
        forgot_password.setPaintFlags(forgot_password.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        text_password_error.setPaintFlags(text_password_error.getPaintFlags()|Paint.UNDERLINE_TEXT_FLAG);

        text_mobile_error.setVisibility(View.INVISIBLE);
        text_password_error.setVisibility(View.INVISIBLE);
        btn_submit.setOnClickListener(this);
        text_invalid_password.setVisibility(View.INVISIBLE);

        txt_title.setTypeface(custom_font_light);
        edit_mobile.setTypeface(custom_font_light);
        text_password_error.setTypeface(custom_font_light);
        text_mobile_error.setTypeface(custom_font_light);
        edit_userpass.setTypeface(custom_font_light);
        btn_submit.setTypeface(custom_font_light);
        text_invalid_password.setTypeface(custom_font_light);
        forgot_password.setTypeface(custom_font_light);

        //create_account.setTypeface(custom_font_light);


        /**************************** Password Show  ****************************/

        chech_box.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                if (is_hidden) {
                    edit_userpass.setInputType(129);
                    edit_userpass.setTypeface(custom_font_light);
                    is_hidden = false;
                } else {
                    edit_userpass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    edit_userpass.setTypeface(custom_font_light);
                    is_hidden= true;
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
                    text_password_error.setText("should be atleast 8 characters long");
                    text_password_error.setVisibility(View.VISIBLE);
                    isValidPassword= false;
               } else{
                    //text_invalid_password.setText("Should contain atleast one alphabet,Number and special characters");
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
        if (id == R.id.action_settings) {
            return true;
        }

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
            login();
        }
    }

    public void setupAuthentication() {
        AuthenticationManager.getInstance().setAuthenticationType(AuthenticationType.OAUTH);
        AuthenticationManager.getInstance().setLoginURL(loginURL);
        AuthenticationManager.getInstance().setRenewURL(renewURL);
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
        try {
            if (Objects.equals((String) response.get("statusDesc"), "account already exist")) {
                Toast.makeText(getApplicationContext(), "User already registered", Toast.LENGTH_LONG).show();
            } else {
                Log.i(TAG, "Login Successful");
                startActivity(new Intent(getApplicationContext(), OtpActivity.class));
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
}