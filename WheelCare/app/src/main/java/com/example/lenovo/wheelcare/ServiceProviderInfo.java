package com.example.lenovo.wheelcare;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import org.w3c.dom.Text;
import org.w3c.dom.TypeInfo;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by Vimal on 08-09-2017.
 */

public class ServiceProviderInfo extends RootActivity {

    private Typeface calibri;

    private int ServiceProviderID;
    private String Name = "Service Provider Name";
    private String Distance = "1.2";
    private String Address = "One two three four five six seven eight nine ten eleven twelve";
    private String PhoneNumber = "9743722774";
    private String Website = "serviceprovider.com";
    private Bitmap ProviderImage = null;
    HashMap<String, Integer> SlotDetails = null;
    Date CurrentDate;

    // MARK: Initialization

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_info);
        calibri = Typeface.createFromAsset(getApplicationContext().getAssets(), "Calibri.ttf");
        //setupIntentData();
        setupToolbar();
        setupListView();
        setupServiceButton();
    }

    // MARK: Setup Intent Data

    private void setupIntentData() {
        Bundle extras = getIntent().getExtras();
        ServiceProviderID = extras.getInt("serviceProviderID");
        Name = extras.getString("name");
        Distance = extras.getString("distance");
        Address = extras.getString("address");
        PhoneNumber = extras.getString("phone_number");
        Website = extras.getString("website");
        ProviderImage = BitmapFactory.decodeByteArray(getIntent().getByteArrayExtra("image"), 0, getIntent().getByteArrayExtra("image").length);
        SlotDetails = (HashMap<String, Integer>) getIntent().getSerializableExtra("slotDetails");
        CurrentDate = (Date) getIntent().getSerializableExtra("currentDate");
    }

    // MARK: Setup Tool Bar

    private void setupToolbar() {
        TextView title = (TextView) findViewById(R.id.toolbar_title);
        title.setText("Provider Information");
    }

    // MARK: Setup List View

    private void setupListView() {
        ListView listView = (ListView) findViewById(R.id.listView);
        InfoAdapter serviceProviderInfoAdapter = new InfoAdapter();
        listView.setAdapter(serviceProviderInfoAdapter);
    }

    private class InfoAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (position == 0) {
                convertView = getLayoutInflater().inflate(R.layout.service_provider_image, null);
                ImageView serviceProviderImage = (ImageView) convertView.findViewById(R.id.serviceProviderImage);
                TextView serviceProviderName = (TextView) convertView.findViewById(R.id.serviceProviderName);
                TextView serviceProviderDistance = (TextView) convertView.findViewById(R.id.serviceProviderDistance);

                serviceProviderName.setTypeface(calibri);
                serviceProviderDistance.setTypeface(calibri);

                if (ProviderImage != null) {
                    serviceProviderImage.setImageBitmap(ProviderImage);
                }
                serviceProviderName.setText(Name);
                serviceProviderDistance.setText(Distance);
                if (Distance.contentEquals("1.0") || Distance.contentEquals("1")) {
                    serviceProviderDistance.append(" km");
                } else {
                    serviceProviderDistance.append(" kms");
                }
            } else if(position == 4) {
                convertView = getLayoutInflater().inflate(R.layout.service_provider_services, null);
                ImageView detailTypeImage = (ImageView) convertView.findViewById(R.id.detailTypeImage);
                TextView servicesOffered = (TextView) convertView.findViewById(R.id.servicesOffered);
                TextView wheelAlignment = (TextView) convertView.findViewById(R.id.wheelAlginment);
                TextView wheelBalancing = (TextView) convertView.findViewById(R.id.wheelBalancing);

                detailTypeImage.setImageResource(R.drawable.settings);
                servicesOffered.setTypeface(calibri, Typeface.BOLD);
                wheelAlignment.setTypeface(calibri);
                wheelBalancing.setTypeface(calibri);

            } else {
                convertView = getLayoutInflater().inflate(R.layout.service_provider_details, null);
                ImageView detailTypeImage = (ImageView) convertView.findViewById(R.id.detailTypeImage);
                TextView detailTypeText = (TextView) convertView.findViewById(R.id.detailTypeText);

                detailTypeText.setTypeface(calibri);

                switch (position) {
                    case 1:
                        detailTypeImage.setImageResource(R.drawable.location);
                        detailTypeText.setMaxLines(3);
                        detailTypeText.setSingleLine(false);
                        detailTypeText.setHorizontallyScrolling(false);
                        int lines = detailTypeText.getLineCount();
                        if(lines == 2) {
                            detailTypeText.setHeight(60);
                        } else if(lines == 3) {
                            detailTypeText.setHeight(70);
                        }
                        detailTypeText.setText(Address);
                        break;

                    case 2:
                        detailTypeImage.setImageResource(R.drawable.leftcall);
                        detailTypeText.setText(PhoneNumber);
                        break;

                    case 3:
                        detailTypeImage.setImageResource(R.drawable.web);
                        detailTypeText.setText(Website);
                        break;
                }
            }
            return convertView;
        }
    }

    // MARK: Setup Service Button

    private void setupServiceButton() {
        Button serviceButton = (Button) findViewById(R.id.proceedToService);
        serviceButton.setTypeface(calibri);
    }

    // MARK: Button Pressed

    public void proceedToService(View view) {
        // Send information as Intent to the next screen
        // Service Provider ID, Current Date, Slots with details
        // Wheel Alignment and Wheel Balancing Amount
        Intent i = new Intent(getApplicationContext(), SelectServices.class);
        startActivity(i);
    }
}