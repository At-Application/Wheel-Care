package com.example.lenovo.wheelcare;

import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.security.PrivateKey;

/**
 * Created by Lenovo on 8/9/2017.
 */

public class CarRegistration extends BaseActivity implements View.OnClickListener{
    private Typeface custom_font_light;
    private ArrayAdapter<CharSequence> adapter;
    private Spinner brand_spinner,model_spinner;
    private final String [] model_items = new String[2];
    private ImageView carImage;
    private TextView txt_title;

   // private String[] car_models= new String[5];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_registration);
        txt_title= (TextView)findViewById(R.id.txt_title);
        brand_spinner = (Spinner) findViewById(R.id.brand_spinner);
        model_spinner= (Spinner)findViewById(R.id.model_spinner);
       // spinner_text= (TextView)findViewById(R.id.spinner_text);
        carImage = (ImageView)findViewById(R.id.carImage);
        custom_font_light = Typeface.createFromAsset(getApplicationContext().getAssets(), "serenity-light.ttf");

        txt_title.setText("Wheelcare Car Registration");
        txt_title.setTypeface(custom_font_light);
// Create an ArrayAdapter using the string array and a default spinner layout

        /*adapter=ArrayAdapter.createFromResource(this,
                    R.array.car_brand_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/
// Apply the adapter to the spinner
        changeTypeFaceSpinner1();
        //changeTypeFaceSpinner2();
        brand_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                Toast.makeText(adapterView.getContext(),
                        "OnItemSelectedListener : " + adapterView.getItemAtPosition(pos).toString(),
                        Toast.LENGTH_LONG).show();
                        changeTypeFaceSpinner2();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        model_spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    public void changeTypeFaceSpinner2(){


        carImage.setVisibility(View.GONE);
        model_items[0]="Select Models";

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_text, model_items) {

            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (model_items[position]== "Brezza"){
                    carImage.setVisibility(View.VISIBLE);
                    carImage.setImageResource(R.drawable.marutibrezza);
                }else if (model_items[position]== "Bolero"){
                    carImage.setVisibility(View.VISIBLE);
                    carImage.setImageResource(R.drawable.mahindrabolero);
                }else if(model_items[position]=="KWID"){
                    carImage.setVisibility(View.VISIBLE);
                    carImage.setImageResource(R.drawable.renaultkwid);
                }
                ((TextView) v).setTypeface(custom_font_light);
                return v;
            }

            public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                View v =super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTypeface(custom_font_light);
                return v;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        model_spinner.setAdapter(adapter);
    }

    public void changeTypeFaceSpinner1(){
        final String [] items = new String[4];
        items[0]="Select Brand";
        items[1]="Maruti";
        items[2]="Mahindra";
        items[3]="Renault";
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_text, items) {

            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (position==1){
                    Log.d("position :", String.valueOf(position));
                    //car_models[1]="Brezza";
                    model_items[1]="Brezza";
                }else if (position == 2){
                    model_items[1]="Bolero";
                }else if (position == 3){
                    model_items[1]="KWID";
                }
                ((TextView) v).setTypeface(custom_font_light);
                return v;
            }

            public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                View v =super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTypeface(custom_font_light);
                return v;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brand_spinner.setAdapter(adapter);
    }
}
