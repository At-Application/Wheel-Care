package com.example.lenovo.wheelcare;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import static android.graphics.Typeface.BOLD;

/**
 * Created by Vimal on 08-09-2017.
 */

public class ServiceInfo extends RootActivity {

    private Typeface calibri;

    private int ServiceProviderID;
    private String RegistrationNumber = "KA 04 JD 1234";
    private String Name = "Service Provider Name";
    private String Address = "One two three four five six seven eight nine ten eleven twelve";
    private String PhoneNumber = "9743722774";
    private String Website = "serviceprovider.com";
    private Bitmap ProviderImage = null;
    private ArrayList<ServiceType> serviceTypes;
    private ServiceStatus serviceStatus = ServiceStatus.FINALIZING;
    String date, boldCode;

    Context context;

    // MARK: Initialization

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_info);
        calibri = Typeface.createFromAsset(getApplicationContext().getAssets(), "Calibri.ttf");
        context = getApplicationContext();
        setupIntentData();
        setupToolbar();
        setupListView();
        setupServiceButton();
    }

    // MARK: Setup Intent Data

    private void setupIntentData() {
        for(UserCarList obj:((GlobalClass)context).userCarLists) {
            if (obj.getServiceStatus() != null){
                RegistrationNumber = obj.getRegistrationNumber();
                serviceStatus = obj.getServiceStatus();
                date = obj.getSlot();
                boldCode = obj.getCode();
                serviceTypes = obj.getServiceType();
                ServiceProviderID = obj.getServiceProivderID();
                //ProviderImage = obj.getImage();
                break;
            }
        }

        for(ServiceProviderDetails obj: ((GlobalClass)context).serviceProviders) {
            if(obj.getId() == ServiceProviderID) {
                Name = obj.getCompanyName();
                Address = obj.getAddress();
                PhoneNumber = obj.getContactNumber();
                Website = obj.getWebsite();
                ProviderImage = BitmapFactory.decodeByteArray(getIntent().getByteArrayExtra("image"), 0, getIntent().getByteArrayExtra("image").length);
            }
        }
    }

    // MARK: Setup Tool Bar

    private void setupToolbar() {
        TextView title = (TextView) findViewById(R.id.toolbar_title);
        title.setText("Status");
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
                convertView = getLayoutInflater().inflate(R.layout.service_status_info, null);
                ImageView CarImage = (ImageView) convertView.findViewById(R.id.CarImage);
                final TextView registrationNumber = (TextView) convertView.findViewById(R.id.RegistrationNumber);
                final TextView wheelAlignment = (TextView) convertView.findViewById(R.id.WheelAlignmentLabel);
                final TextView wheelBalancing = (TextView) convertView.findViewById(R.id.WheelBalancingLabel);
                final TextView code = (TextView) convertView.findViewById(R.id.Code);
                final TextView codeVerified = (TextView) convertView.findViewById(R.id.CodeVerified);
                final TextView dateSlot = (TextView) convertView.findViewById(R.id.dateSlot);
                final TextView started = (TextView) convertView.findViewById(R.id.Started);
                final TextView inProgress = (TextView) convertView.findViewById(R.id.InProgress);
                final TextView finalizing = (TextView) convertView.findViewById(R.id.Finalizing);
                final TextView done = (TextView) convertView.findViewById(R.id.Done);

                registrationNumber.setTypeface(calibri);
                codeVerified.setTypeface(calibri);
                wheelAlignment.setTypeface(calibri);
                wheelBalancing.setTypeface(calibri);
                code.setTypeface(calibri, BOLD);
                dateSlot.setTypeface(calibri);

                started.setTextSize(12);
                inProgress.setTextSize(12);
                finalizing.setTextSize(12);
                done.setTextSize(12);
                code.setTextSize(25);

                if (ProviderImage != null) {
                    CarImage.setImageBitmap(ProviderImage);
                }
                registrationNumber.setText(RegistrationNumber);

                dateSlot.setText(date);

                code.setText("Code: " + boldCode);

                if(serviceTypes.contains(ServiceType.WHEEL_ALIGNMENT)) {
                    wheelAlignment.setVisibility(View.VISIBLE);
                } else {
                    wheelAlignment.setVisibility(View.INVISIBLE);
                }

                if(serviceTypes.contains(ServiceType.WHEEL_BALANCING)) {
                    wheelBalancing.setVisibility(View.VISIBLE);
                } else {
                    wheelBalancing.setVisibility(View.INVISIBLE);
                }

                switch (serviceStatus) {
                    case DONE:
                        done.setBackground(getDrawable(R.color.green));
                        done.setClickable(false);

                    case FINALIZING:
                        finalizing.setBackground(getDrawable(R.drawable.border_green));
                        finalizing.setClickable(false);

                    case IN_PROGRESS:
                        inProgress.setBackground(getDrawable(R.drawable.border_green));
                        inProgress.setClickable(false);

                    case STARTED:
                        started.setBackground(getDrawable(R.drawable.border_green));
                        started.setClickable(false);

                    case VERIFIED:
                        codeVerified.setText("Code Verified");
                        break;

                    default: codeVerified.setText("Code Not Verified");
                }

            } else if(position == 1) {
                convertView = getLayoutInflater().inflate(R.layout.payment_code, null);
                TextView serviceProviderName = (TextView) convertView.findViewById(R.id.Key);
                TextView makeEmpty = (TextView) convertView.findViewById(R.id.Value);
                makeEmpty.setVisibility(View.INVISIBLE);

                serviceProviderName.setTypeface(calibri);
                serviceProviderName.setText(Name);

            } else {
                convertView = getLayoutInflater().inflate(R.layout.service_provider_details, null);
                ImageView detailTypeImage = (ImageView) convertView.findViewById(R.id.detailTypeImage);
                TextView detailTypeText = (TextView) convertView.findViewById(R.id.detailTypeText);

                detailTypeText.setTypeface(calibri);

                switch (position) {
                    case 2:
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

                    case 3:
                        detailTypeImage.setImageResource(R.drawable.leftcall);
                        detailTypeText.setText(PhoneNumber);
                        break;

                    case 4:
                        detailTypeImage.setImageResource(R.drawable.web);
                        detailTypeText.setText(Website);
                        break;

                    default:
                        convertView = new View(getApplicationContext());
                        convertView.setMinimumHeight(5);
                        convertView.setBackgroundResource(R.color.white);
                }
            }
            return convertView;
        }
    }

    // MARK: Setup Service Button

    private void setupServiceButton() {
        Button serviceButton = (Button) findViewById(R.id.proceedToService);
        serviceButton.setText("CANCEL");
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