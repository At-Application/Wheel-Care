package com.example.lenovo.wheelcare;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.text.SimpleDateFormat;

import static android.graphics.Typeface.BOLD;

public class PendingServices extends Activity implements PendingServicesListener {

    private static final String TAG = PendingServices.class.getSimpleName();

    private Typeface calibri;

    private SwipeRefreshLayout mySwipeRefreshLayout;

    private PendingServiceAdapter adapter;

    public PendingServicesListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_provider_dashboard);
        listener = this;
        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("Refresh", "onRefresh called from SwipeRefreshLayout");
                        getListVehicleDetails();
                    }
                }
        );
        calibri = Typeface.createFromAsset(getApplicationContext().getAssets(), "Calibri.ttf");
        getListVehicleDetails();
        setupListView();
    }

    // TODO: Fetch the list from the server

    private void getListVehicleDetails() {
        ((GlobalClass)getApplicationContext()).getServicesDetails(this);
    }

    // TODO: Freeze Service Provider for the day

    public void freezeServices(View view) {

    }

    // MARK: List view related functions

    private void setupListView() {
        ListView listView = (ListView) findViewById(R.id.pending_services_listview);
        adapter = new PendingServiceAdapter();
        listView.setAdapter(adapter);
    }

    // After the data is received from server, reloading the list view.

    @Override
    public void RetrievedServices() {
        if(mySwipeRefreshLayout.isRefreshing()) {
            mySwipeRefreshLayout.setRefreshing(false);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void RetrievingServicesFailed(VolleyError error) {
        if(mySwipeRefreshLayout.isRefreshing()) {
            mySwipeRefreshLayout.setRefreshing(false);
        }
        Log.e(TAG, error.toString());
    }

    private class PendingServiceAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return (((GlobalClass)getApplicationContext()).pending.size() + 1);
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int pos, View view, ViewGroup viewGroup) {

            final int i = pos;
            if(i > 0) {
                final VehicleDetails service = ((GlobalClass)getApplicationContext()).pending.get(i-1);

                SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy(kk:mm)");
                String date = fmt.format(service.date_slot);

                switch(service.serviceStatus) {

                    case NOT_VERIFIED:
                        {
                            view = getLayoutInflater().inflate(R.layout.pending_services_listview_row, null);
                            final Button verifyButton = (Button) view.findViewById(R.id.ButtonVerifyCode);
                            final Button cancel = (Button) view.findViewById(R.id.ButtonCancel);
                            final Button verify = (Button) view.findViewById(R.id.ButtonContinue);
                            final TextView registrationNumber = (TextView) view.findViewById(R.id.vehiclenumber);
                            final TextView dateSlot = (TextView) view.findViewById(R.id.date_slot);
                            final EditText code = (EditText) view.findViewById(R.id.code);

                            verifyButton.setTypeface(calibri);
                            registrationNumber.setTypeface(calibri);
                            dateSlot.setTypeface(calibri);
                            code.setTypeface(calibri);

                            registrationNumber.setText(service.vehicleRegistrationNumber);
                            dateSlot.setText(date);

                            verifyButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    verifyButton.setVisibility(View.INVISIBLE);
                                    cancel.setVisibility(View.VISIBLE);
                                    verify.setVisibility(View.VISIBLE);
                                    code.setVisibility(View.VISIBLE);
                                }
                            });

                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    verifyButton.setVisibility(View.VISIBLE);
                                    cancel.setVisibility(View.INVISIBLE);
                                    verify.setVisibility(View.INVISIBLE);
                                    code.setVisibility(View.INVISIBLE);
                                }
                            });

                            verify.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (code.getText().toString().trim().equals(service.code)) {
                                        service.serviceStatus = ServiceStatus.VERIFIED;
                                        ((GlobalClass) getApplicationContext()).pending.set((i - 1), service);
                                        ((GlobalClass)getApplicationContext()).setServicesStatus(listener, service);
                                    } else {
                                        code.setText("Failed");
                                    }
                                    notifyDataSetChanged();
                                }
                            });
                        }
                        break;

                    case VERIFIED:
                    case STARTED:
                    case IN_PROGRESS:
                    case FINALIZING:
                        {
                            view = getLayoutInflater().inflate(R.layout.pending_service_info_detail_view, null);
                            final TextView registrationNumber = (TextView) view.findViewById(R.id.vehiclenumber);
                            final TextView username = (TextView) view.findViewById(R.id.username);
                            final TextView wheelAlignment = (TextView) view.findViewById(R.id.WheelAlignment);
                            final TextView wheelBalancing = (TextView) view.findViewById(R.id.WheelBalancing);
                            final TextView code = (TextView) view.findViewById(R.id.Code);
                            final TextView dateSlot = (TextView) view.findViewById(R.id.date_slot);
                            final ImageView vehicleImage = (ImageView) view.findViewById(R.id.Vehicle);
                            final Button started = (Button) view.findViewById(R.id.Started);
                            final Button inProgress = (Button) view.findViewById(R.id.InProgress);
                            final Button finalizing = (Button) view.findViewById(R.id.Finalizing);
                            final Button done = (Button) view.findViewById(R.id.Done);
                            final TextView historyStatus = (TextView) view.findViewById(R.id.CompletionStatus);

                            historyStatus.setVisibility(View.INVISIBLE);

                            registrationNumber.setTypeface(calibri);
                            username.setTypeface(calibri, BOLD);
                            wheelAlignment.setTypeface(calibri);
                            wheelBalancing.setTypeface(calibri);
                            code.setTypeface(calibri, BOLD);
                            dateSlot.setTypeface(calibri);

                            started.setTextSize(12);
                            inProgress.setTextSize(12);
                            finalizing.setTextSize(12);
                            done.setTextSize(12);
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

                            started.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    service.serviceStatus = ServiceStatus.STARTED;
                                    ((GlobalClass) getApplicationContext()).pending.set(i - 1, service);
                                    ((GlobalClass)getApplicationContext()).setServicesStatus(listener, service);
                                    notifyDataSetChanged();
                                }
                            });

                            inProgress.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    service.serviceStatus = ServiceStatus.IN_PROGRESS;
                                    ((GlobalClass) getApplicationContext()).pending.set(i - 1, service);
                                    ((GlobalClass)getApplicationContext()).setServicesStatus(listener, service);
                                    notifyDataSetChanged();
                                }
                            });

                            finalizing.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    service.serviceStatus = ServiceStatus.FINALIZING;
                                    ((GlobalClass) getApplicationContext()).pending.set(i - 1, service);
                                    ((GlobalClass)getApplicationContext()).setServicesStatus(listener, service);
                                    notifyDataSetChanged();
                                }
                            });

                            done.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    service.serviceStatus = ServiceStatus.DONE;
                                    ((GlobalClass) getApplicationContext()).pending.set(i - 1, service);
                                    ((GlobalClass)getApplicationContext()).setServicesStatus(listener, service);
                                    notifyDataSetChanged();
                                }
                            });

                            switch (service.serviceStatus) {
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
                            }
                        }
                        break;

                    case DONE:
                    case DISMISS:
                        {
                            view = null;
                        }
                        break;
                }
            } else {
                view = new View(getApplicationContext());
                view.setMinimumHeight(5);
                view.setBackgroundResource(R.color.white);
            }

            return view;
        }
    }
}
