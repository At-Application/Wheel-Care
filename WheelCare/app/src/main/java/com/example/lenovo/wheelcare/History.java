package com.example.lenovo.wheelcare;

import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.graphics.Typeface.BOLD;

/**
 * Created by Vimal on 10-09-2017.
 */

public class History extends RootActivity {

    private Typeface calibri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        calibri = Typeface.createFromAsset(getApplicationContext().getAssets(), "Calibri.ttf");
        setupListView();
    }

    // MARK: List view related functions

    private void setupListView() {
        ListView listView = (ListView) findViewById(R.id.pending_services_listview);
        HistoryAdapter adapter = new HistoryAdapter();
        listView.setAdapter(adapter);
    }

    private class HistoryAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            Log.d("SIZE", String.valueOf(((GlobalClass)getApplicationContext()).history.size()));
            return ((GlobalClass)getApplicationContext()).history.size();
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
        public View getView(int position, View view, ViewGroup parent) {

            final VehicleDetails service = ((GlobalClass)getApplicationContext()).history.get(position);

            if(service.serviceStatus == ServiceStatus.DONE || service.serviceStatus == ServiceStatus.DISMISS) {

                SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy(kk:mm)");
                String date = fmt.format(service.date_slot);

                view = getLayoutInflater().inflate(R.layout.pending_service_info_detail_view, null);
                final TextView registrationNumber = (TextView) view.findViewById(R.id.vehiclenumber);
                final TextView username = (TextView) view.findViewById(R.id.username);
                final TextView wheelAlignment = (TextView) view.findViewById(R.id.WheelAlignment);
                final TextView wheelBalancing = (TextView) view.findViewById(R.id.WheelBalancing);
                final TextView code = (TextView) view.findViewById(R.id.Code);
                final TextView dateSlot = (TextView) view.findViewById(R.id.date_slot);
                final ImageView vehicleImage = (ImageView) view.findViewById(R.id.Vehicle);
                final LinearLayout statusBar = (LinearLayout) view.findViewById(R.id.StatusBar);
                final TextView historyStatus = (TextView) view.findViewById(R.id.CompletionStatus);
                statusBar.setVisibility(View.INVISIBLE);

                registrationNumber.setTypeface(calibri);
                username.setTypeface(calibri, BOLD);
                wheelAlignment.setTypeface(calibri);
                wheelBalancing.setTypeface(calibri);
                code.setTypeface(calibri, BOLD);
                dateSlot.setTypeface(calibri);
                historyStatus.setTypeface(calibri, BOLD);

                historyStatus.setTextSize(23);
                code.setTextSize(25);

                registrationNumber.setText(service.vehicleRegistrationNumber);
                username.setText(service.customername);
                if (service.serviceRequired.contains(ServiceType.WHEEL_ALIGNMENT)) {
                    wheelAlignment.setHeight(20);
                } else {
                    wheelAlignment.setHeight(0);
                }

                if (service.serviceRequired.contains(ServiceType.WHEEL_BALANCING)) {
                    wheelBalancing.setHeight(20);
                } else {
                    wheelBalancing.setHeight(0);
                }
                dateSlot.setText(date);
                code.setText("CODE: ");
                code.append(service.code);
                vehicleImage.setImageBitmap(service.vehicleImage);

                // Main Logic
                if (service.issue != null) {
                    historyStatus.setText(service.issue);
                    historyStatus.setBackground(getDrawable(R.color.red));
                }
            } else {
                view = null;
            }
            return view;
        }
    }
}