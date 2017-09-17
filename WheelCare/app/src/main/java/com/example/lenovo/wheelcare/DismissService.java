package com.example.lenovo.wheelcare;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import static android.graphics.Typeface.BOLD;

/**
 * Created by Vimal on 10-09-2017.
 */

public class DismissService extends RootActivity {

    private Typeface calibri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.issues);
        calibri = Typeface.createFromAsset(getApplicationContext().getAssets(), "Calibri.ttf");
        getIssuesList();
        setupListView();
    }

    private void getIssuesList() {
        ((GlobalClass)getApplicationContext()).getIssueList();
    }

    private void setupListView() {
        ListView listView = (ListView) findViewById(R.id.issuesList);
        IssuesAdapter adapter = new IssuesAdapter();
        listView.setAdapter(adapter);
    }

    private class IssuesAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return ((GlobalClass)getApplicationContext()).issues.size();
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

            if(position > 0) {
                view = getLayoutInflater().inflate(R.layout.issue_list_row, null);
                TextView issueString = (TextView) view.findViewById(R.id.textView);
                issueString.setTypeface(calibri, BOLD);

                issueString.setText(((GlobalClass) getApplicationContext()).issues.get(position - 1));
            } else {
                // TODO: Should be the position from previous screen
                final VehicleDetails service = ((GlobalClass)getApplicationContext()).history.get(position);
                SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy(kk:mm)");
                String date = fmt.format(service.date_slot);

                view = getLayoutInflater().inflate(R.layout.issue_service_detail_view, null);
                final TextView registrationNumber = (TextView) view.findViewById(R.id.vehiclenumber);
                final TextView username = (TextView) view.findViewById(R.id.username);
                final TextView wheelAlignment = (TextView) view.findViewById(R.id.WheelAlignmentCheckBox);
                final TextView wheelBalancing = (TextView) view.findViewById(R.id.WheelBalancingCheckBox);
                final TextView code = (TextView) view.findViewById(R.id.Code);
                final TextView dateSlot = (TextView) view.findViewById(R.id.date_slot);
                final ImageView vehicleImage = (ImageView) view.findViewById(R.id.Vehicle);

                registrationNumber.setTypeface(calibri);
                username.setTypeface(calibri, BOLD);
                wheelAlignment.setTypeface(calibri);
                wheelBalancing.setTypeface(calibri);
                code.setTypeface(calibri, BOLD);
                dateSlot.setTypeface(calibri);

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

                // TODO: Implement On click for sending the status
            }
            return view;
        }
    }
}
