package com.example.lenovo.wheelcare;

import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.security.PrivateKey;
import java.util.Objects;

/**
 * Created by Lenovo on 8/9/2017.
 */

public class CarRegistration extends BaseActivity implements View.OnClickListener{
    private Typeface custom_font_light;
    private ArrayAdapter<CharSequence> adapter;
    public Spinner brand_spinner,model_spinner,type_spinner;
    private final String [] model_items = new String[2];
    private ImageView carImage;
    private TextView txt_title;
    private TextView text_invalid_regno;
    private EditText et_carRegno;
   // private TextView text_register_number;
    private boolean isValidCarNumber= false;
    private Button submit_btn;

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (isValidCarNumber){
            //startActivity()
            Toast.makeText(getApplicationContext(),"Valid Car number",Toast.LENGTH_LONG).show();
        }else if(et_carRegno.getText().toString().length()==0){
            text_invalid_regno.setText("This field cannot be Empty!");
            text_invalid_regno.setVisibility(View.VISIBLE);
        }
    }

    // private String[] car_models= new String[5];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_registration);


        //txt_title= (TextView)findViewById(R.id.txt_title);
        brand_spinner = (Spinner) findViewById(R.id.brand_spinner);
        model_spinner= (Spinner)findViewById(R.id.model_spinner);
        //model_spinner.setClickable(false);

        type_spinner = (Spinner)findViewById(R.id.type_spinner);
        //type_spinner.setClickable(false);
        //text_register_number = (TextView)findViewById(R.id.text_Register_number);
        text_invalid_regno= (TextView)findViewById(R.id.text_invalid_regno);
       // spinner_text= (TextView)findViewById(R.id.spinner_text);
        carImage = (ImageView)findViewById(R.id.carImage);
        et_carRegno= (EditText)findViewById(R.id.et_carRegno);
        submit_btn= (Button)findViewById(R.id.btn_submit);
        custom_font_light = Typeface.createFromAsset(getApplicationContext().getAssets(), "Calibri.ttf");

        //text_register_number.setTypeface(custom_font_light);

        submit_btn.setOnClickListener(this);

        /*txt_title.setText("Wheelcare Car Registration");
        txt_title.setTypeface(custom_font_light);
        */
        text_invalid_regno.setVisibility(View.GONE);

        changeTypeFaceSpinner1();
        changeTypeFaceSpinner2();
        changeTypeFaceSpinner3();

        et_carRegno.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                text_invalid_regno.setText("Invalid Registration number");
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (et_carRegno.getText().toString().matches("^ ")) {
                    text_invalid_regno.setVisibility(View.VISIBLE);
                    isValidCarNumber = false;
                } else if (et_carRegno.getText().toString().length() == 10) {
                    text_invalid_regno.setVisibility(View.INVISIBLE);
                    isValidCarNumber = true;
                } else {
                    isValidCarNumber = false;
                }
                                                   //edit_username.setText("");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        brand_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
               /* Toast.makeText(adapterView.getContext(),
                        "OnItemSelectedListener : " + adapterView.getItemAtPosition(pos).toString(),
                        Toast.LENGTH_LONG).show();*/
                changeTypeFaceSpinner2();
                TextView selectedText = (TextView) adapterView.getChildAt(0);
                if (selectedText != null && selectedText.getText() != "Manufacturer") {
                    selectedText.setTextColor(Color.BLACK);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        model_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
               /* Toast.makeText(adapterView.getContext(),
                        "OnItemSelectedListener : " + adapterView.getItemAtPosition(pos).toString(),
                        Toast.LENGTH_LONG).show();*/
                changeTypeFaceSpinner3();
                TextView selectedText = (TextView) adapterView.getChildAt(0);
                if (selectedText != null && selectedText.getText() != "Vehicle") {
                    selectedText.setTextColor(Color.BLACK);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
               /* Toast.makeText(adapterView.getContext(),
                        "OnItemSelectedListener : " + adapterView.getItemAtPosition(pos).toString(),
                        Toast.LENGTH_LONG).show();*/
                TextView selectedText = (TextView) adapterView.getChildAt(0);
                if (selectedText != null && selectedText.getText() != "Vehicle Type") {
                    selectedText.setTextColor(Color.BLACK);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void changeTypeFaceSpinner2(){


        //carImage.setImageResource(R.drawable.temp_logo);
        model_items[0]="Vehicle";

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_text, model_items) {

            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                type_spinner.setEnabled(true);
                if (Objects.equals(model_items[position], "Brezza")){
                    carImage.setVisibility(View.VISIBLE);
                    carImage.setImageResource(R.drawable.marutibrezza);
                }else if (Objects.equals(model_items[position], "Bolero")){
                    carImage.setVisibility(View.VISIBLE);
                    carImage.setImageResource(R.drawable.mahindrabolero);
                }else if(Objects.equals(model_items[position], "KWID")){
                    carImage.setVisibility(View.VISIBLE);
                    carImage.setImageResource(R.drawable.renaultkwid);
                }else{
                    type_spinner.setEnabled(false);
                    carImage.setVisibility(View.INVISIBLE);
                }
                ((TextView) v).setTypeface(custom_font_light);
                return v;
            }
            @Override
            public boolean isEnabled(int position) {
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }

            public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                View v =super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTypeface(custom_font_light);
                if(position == 0){
                    // Set the hint text color gray
                    ((TextView)v).setTextColor(Color.GRAY);
                }
                else {

                    ((TextView)v).setTextColor(Color.BLACK);
                }

                return v;
            }
        };
        //model_spinner.setEnabled(true);
        //model_spinner.setClickable(true);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        model_spinner.setAdapter(adapter);
    }

    public void changeTypeFaceSpinner3(){
        final String [] items = new String[4];
        items[0]="Vehicle Type";
        items[1]="Diesel";
        items[2]="Petrol";
        items[3]="CNG";
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_text, items) {

            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTypeface(custom_font_light);

                if(position == 0){

                }
                return v;
            }
            @Override
            public boolean isEnabled(int position) {
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }

            public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                View v =super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTypeface(custom_font_light);

                if(position == 0){
                    // Set the hint text color gray
                    ((TextView)v).setTextColor(Color.GRAY);
                }
                else {

                    ((TextView)v).setTextColor(Color.BLACK);
                }
                return v;
            }
        };
        //type_spinner.setEnabled(true);
        //type_spinner.setClickable(true);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_spinner.setAdapter(adapter);
    }
    public void changeTypeFaceSpinner1(){
        final String [] items = new String[4];
        items[0]="Manufacturer";
        items[1]="Maruti";
        items[2]="Mahindra";
        items[3]="Renault";
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_text, items) {

            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                if (position == 0) {
                    //Log.d("position :", String.valueOf(position));
                    //car_models[1]="Brezza";
                    model_items[1] = "";
                    model_spinner.setEnabled(false);
                    type_spinner.setEnabled(false);
                } else {
                    model_spinner.setEnabled(true);
                    if (position == 1) {
                        model_items[1] = "Brezza";
                    } else if (position == 2) {
                        model_items[1] = "Bolero";
                    } else if (position == 3) {
                        model_items[1] = "KWID";
                    }
                }

                ((TextView) v).setTypeface(custom_font_light);

                return v;
            }

            @Override
            public boolean isEnabled(int position) {
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {

                    return true;
                }
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v =super.getDropDownView(position, convertView, parent);
                Log.d("positon", String.valueOf(position));
                ((TextView) v).setTypeface(custom_font_light);
                if(position == 0){
                    // Set the hint text color gray
                    ((TextView)v).setTextColor(Color.GRAY);
                }
                else {
                    Log.d("position_select", String.valueOf(position));
                    ((TextView)v).setTextColor(Color.BLACK);
                }

                return v;
            }
        };

        //type_spinner.setClickable(false);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brand_spinner.setAdapter(adapter);
    }
}
