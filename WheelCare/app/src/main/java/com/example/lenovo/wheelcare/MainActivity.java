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
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends RootActivity implements View.OnClickListener {

    private EditText edit_username;
    private EditText edit_userpass;
    private Button btn_login;
    private CheckBox chech_box;
    private TextView forgot_password;
    private TextView create_account;
    private TextView text_header_username;
    private TextView txt_password_header;
    private Typeface custom_font_bold,custom_font_light;
    private TextView text_invalid_password;
    private boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);
        edit_username =(EditText)findViewById(R.id.edit_username);
        edit_userpass =(EditText)findViewById(R.id.edit_userpass);

        custom_font_light = Typeface.createFromAsset(getApplicationContext().getAssets(), "serenity-light.ttf");
        custom_font_bold = Typeface.createFromAsset(getApplicationContext().getAssets(), "serenity-bold.ttf");

        btn_login = (Button)findViewById(R.id.btn_login);
        chech_box = (CheckBox) findViewById(R.id.chech_box);
        forgot_password = (TextView)findViewById(R.id.text_forgot_password);
        create_account = (TextView)findViewById(R.id.text_create_account);
        text_header_username = (TextView)findViewById(R.id.text_header_username);
        txt_password_header = (TextView)findViewById(R.id.txt_password_header);
        text_invalid_password = (TextView)findViewById(R.id.text_invalid_password);
        forgot_password.setPaintFlags(forgot_password.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        create_account.setPaintFlags(create_account.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        //forgot_password.setOnClickListener(this);
        create_account.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        text_invalid_password.setVisibility(View.INVISIBLE);

        edit_username.setTypeface(custom_font_light);
        edit_userpass.setTypeface(custom_font_light);
        chech_box.setTypeface(custom_font_light);
        btn_login.setTypeface(custom_font_light);
        text_invalid_password.setTypeface(custom_font_light);
        text_header_username.setTypeface(custom_font_light);
        txt_password_header.setTypeface(custom_font_light);
        text_invalid_password.setTypeface(custom_font_light);
        forgot_password.setTypeface(custom_font_light);
        create_account.setTypeface(custom_font_light);


        /**************************** Password Show  ****************************/
        chech_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    edit_userpass.setInputType(129);
                    edit_userpass.setTypeface(custom_font_light);

                } else {
                    edit_userpass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    edit_userpass.setTypeface(custom_font_light);
                }
            }
        });
        /*******************************************************************************/
       /* edit_username.addTextChangedListener(new TextWatcher(){
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                edit_username.setBackgroundColor(Color.parseColor("#ffffff"));
                text_invalid_password.setVisibility(View.INVISIBLE);
                if (edit_username.getText().toString().matches("^ ") )
                    edit_username.setText("");
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            @Override
            public void afterTextChanged(Editable arg0) {

            }
        });

        edit_userpass.addTextChangedListener(new TextWatcher(){
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                //Log.d("Text changed to:", (String) s);
                Log.d("MSG","text changed");
                edit_username.setBackgroundColor(Color.parseColor("#ffffff"));
                if (edit_userpass.getText().toString().matches("^[A-Za-z.\\s_-]+$") ){
                    text_invalid_password.setVisibility(View.INVISIBLE);
                }else{
                    edit_userpass.setText("");
                    text_invalid_password.setVisibility(View.VISIBLE);
                }


            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            @Override
            public void afterTextChanged(Editable arg0) {

            }
        });*/

        /*edit_username.setFilters(new InputFilter[] {
                new InputFilter() {
                    public CharSequence filter(CharSequence src, int start,
                                               int end, Spanned dst, int dstart, int dend) {
                        if(src.equals("")){ // for backspace
                            return src;
                        }
                        if(src.toString().matches("[\\x00-\\x7F]+")){
                            return src;
                        }
                        return "";
                    }
                }
        });
        edit_userpass.setFilters(new InputFilter[] {
                new InputFilter() {
                    public CharSequence filter(CharSequence src, int start,
                                               int end, Spanned dst, int dstart, int dend) {
                        if(src.equals("")){ // for backspace
                            return src;
                        }
                        if(src.toString().matches("[\\x00-\\x7F]+")){
                            return src;
                        }
                        return "";
                    }
                }
        });*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btn_login :
               if(edit_username.getText().toString().matches("^ @$")){
                   text_invalid_password.setVisibility(View.VISIBLE);
               }

                //startActivity(intent);
                break;

            /*case R.id.text_forgot_password :
                break;*/
            case R.id.text_create_account :
                startActivity(new Intent(getApplicationContext(),RegisterationActicvity.class));
                break;


        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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


}
