package com.wheelcare.wheelcare;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wheelcare.wheelcare.R;

import java.text.SimpleDateFormat;

import static android.graphics.Typeface.BOLD;

/**
 * Created by Vimal on 10-09-2017.
 */

public class DismissService extends RootActivity {

    private Typeface calibri;
    private Button dismiss_submit_btn;
    private int removePosition;
    private Issues issue;
    private TextView comment;

    private VehicleDetails service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.issues);

        calibri = Typeface.createFromAsset(getApplicationContext().getAssets(), "Calibri.ttf");
        dismiss_submit_btn= (Button)findViewById(R.id.dismissbutton);
        getIssuesList();
        setupListView();

        Bundle bundle = getIntent().getExtras();
        removePosition = bundle.getInt("position");
        service = ((GlobalClass)getApplicationContext()).pending.get(removePosition);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupToolbar();

        comment = (TextView)findViewById(R.id.Comments);

        dismiss_submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GlobalClass)getApplicationContext()).history.add(((GlobalClass)getApplicationContext()).pending.get(removePosition - 1));
                ((GlobalClass)getApplicationContext()).pending.remove(removePosition);
                service.serviceStatus = ServiceStatus.DISMISS;
                service.issue = issue.issue;
                service.comment = comment.getText().toString();
                ((GlobalClass)getApplicationContext()).pending.get(removePosition).serviceStatus = ServiceStatus.DISMISS;
                ((GlobalClass)getApplicationContext()).pending.get(removePosition).issue = issue.issue;
                ((GlobalClass)getApplicationContext()).pending.get(removePosition).comment = comment.getText().toString();
                ((GlobalClass)getApplicationContext()).setServicesStatus(service);
            }
        });
    }

    private void setupToolbar() {
        TextView title = (TextView) findViewById(R.id.toolbar_title);
        title.setText("Dismiss Service");
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
        public View getView(final int position, View view, ViewGroup parent) {

            if(position > 0) {
                view = getLayoutInflater().inflate(R.layout.issue_list_row, null);
                TextView issueString = (TextView) view.findViewById(R.id.textView);
                ImageView tick = (ImageView) view.findViewById(R.id.tick);
                issueString.setTypeface(calibri, BOLD);

                issueString.setText(((GlobalClass) getApplicationContext()).issues.get(position - 1).issue);
                tick.setVisibility(((GlobalClass) getApplicationContext()).issues.get(position - 1).status == true ? View.VISIBLE : View.INVISIBLE);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for(int j = 0; j < ((GlobalClass) getApplicationContext()).issues.size(); j++)
                            ((GlobalClass) getApplicationContext()).issues.get(j).status = false;
                        ((GlobalClass) getApplicationContext()).issues.get(position - 1).status = true;
                        issue = ((GlobalClass) getApplicationContext()).issues.get(position - 1);
                        notifyDataSetChanged();
                    }
                });
            } else {
                // TODO: Should be the position from previous screen
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

                int pos = 0;
                for(; pos < ((GlobalClass)getApplicationContext()).vehicles.size(); pos++) {
                    if(((GlobalClass)getApplicationContext()).vehicles.get(pos).id == service.model_id) {
                        break;
                    }
                }

                byte[] image = ((GlobalClass)getApplicationContext()).vehicles.get(pos).image;
                Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
                vehicleImage.setImageBitmap(bmp);
            }
            return view;
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
