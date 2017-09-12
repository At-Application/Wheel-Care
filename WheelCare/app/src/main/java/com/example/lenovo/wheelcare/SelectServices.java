package com.example.lenovo.wheelcare;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by Vimal on 08-09-2017.
 */

public class SelectServices extends RootActivity {

    private Typeface calibri;

    private int ServiceProviderID;
    private String RegistrationNumber = "KA04JD1234";
    private String Slot = "14 - 15";
    private String WheelAlignmentAmount = "300";
    private String WheelBalancingAmount = "300";
    private String Total = "";
    private Bitmap SelectedCarImage = null;
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
        WheelAlignmentAmount = extras.getString("wheelAlignmentAmount");
        WheelBalancingAmount = extras.getString("wheelBalancingAmount");
        SlotDetails = (HashMap<String, Integer>) getIntent().getSerializableExtra("slotDetails");
        CurrentDate = (Date) getIntent().getSerializableExtra("currentDate");
    }

    // MARK: Setup Tool Bar

    private void setupToolbar() {
        TextView title = (TextView) findViewById(R.id.toolbar_title);
        title.setText("Select Services");
    }

    // MARK: Setup List View

    private void setupListView() {
        ListView listView = (ListView) findViewById(R.id.listView);
        SelectAdapter adapter = new SelectAdapter();
        listView.setAdapter(adapter);
    }

    private class SelectAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 2;
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
                TextView selectedCar = (TextView) convertView.findViewById(R.id.serviceProviderName);
                TextView registrationNumber = (TextView) convertView.findViewById(R.id.serviceProviderDistance);

                selectedCar.setTypeface(calibri);
                registrationNumber.setTypeface(calibri);
                registrationNumber.setTextColor(Color.BLACK);
                registrationNumber.setTextSize(18);

                if (SelectedCarImage != null) {
                    serviceProviderImage.setImageBitmap(SelectedCarImage);
                }
                selectedCar.setText("Selected Car");
                registrationNumber.setText(RegistrationNumber);

            } else if(position == 1) {
                convertView = getLayoutInflater().inflate(R.layout.slot_details, null);
                TextView date = (TextView) convertView.findViewById(R.id.Date);
                TextView selectedDate = (TextView) convertView.findViewById(R.id.SelectedDate);
                TextView selectedSlot = (TextView) convertView.findViewById(R.id.SelectedSlot);
                TextView slot = (TextView) convertView.findViewById(R.id.slot);

                date.setTypeface(calibri);
                selectedDate.setTypeface(calibri);
                selectedDate.setTypeface(calibri);
                slot.setTypeface(calibri);

            } else if(position == 2) {

            } else if(position == 3) {

            } else {

            }
            return convertView;
        }
    }

    // MARK: Setup Service Button

    private void setupServiceButton() {
        Button serviceButton = (Button) findViewById(R.id.proceedToService);
        serviceButton.setTypeface(calibri);
        serviceButton.setText("PROCEED TO PAY");
    }

    // MARK: Button Pressed

    public void proceedToService(View view) {

    }

}
