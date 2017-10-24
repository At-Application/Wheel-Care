package com.wheelcare.wheelcare;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wheelcare.wheelcare.R;

import java.text.SimpleDateFormat;

import static android.graphics.Typeface.BOLD;

/**
 * Created by Vimal on 10-09-2017.
 */

public class UserHistory extends Fragment {

    private Typeface calibri;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        calibri = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "Calibri.ttf");
        setupListView(view);
    }

    // MARK: List view related functions

    private void setupListView(View view) {
        ListView listView = (ListView) view.findViewById(R.id.user_history_listview);
        HistoryAdapter adapter = new HistoryAdapter();
        listView.setAdapter(adapter);
    }

    private class HistoryAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            Log.d("SIZE", String.valueOf(((GlobalClass)getActivity().getApplicationContext()).userhistory.size()));

            return ((GlobalClass)getActivity().getApplicationContext()).userhistory.size();
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

            final VehicleDetails service = ((GlobalClass)getActivity().getApplicationContext()).history.get(position);

            if(service.serviceStatus == ServiceStatus.DONE || service.serviceStatus == ServiceStatus.DISMISS) {

                SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy(kk:mm)");
                String date = fmt.format(service.date_slot);

                view = getLayoutInflater(null).inflate(R.layout.user_history_detail_view, null);
                final TextView registrationNumber = (TextView) view.findViewById(R.id.vehiclenumber);
                final TextView username = (TextView) view.findViewById(R.id.username);
                final TextView wheelAlignment = (TextView) view.findViewById(R.id.WheelAlignmentCheckBox);
                final TextView wheelBalancing = (TextView) view.findViewById(R.id.WheelBalancingCheckBox);
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
                    historyStatus.setBackground(getActivity().getDrawable(R.color.red));
                }
            } else {
                view = null;
            }
            return view;
        }
    }
}