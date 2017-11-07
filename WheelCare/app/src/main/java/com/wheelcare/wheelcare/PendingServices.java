package com.wheelcare.wheelcare;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import com.android.volley.VolleyError;
import com.wheelcare.wheelcare.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static android.graphics.Typeface.BOLD;

public class PendingServices extends Fragment implements PendingServicesListener {

    private static final String TAG = PendingServices.class.getSimpleName();

    private Typeface calibri;

    private SwipeRefreshLayout mySwipeRefreshLayout;

    private PendingServiceAdapter adapter;

    public PendingServicesListener listener;

    private Button freezeButton;
    private Button temporaryFreezeButton;

    private ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.service_provider_dashboard, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listener = this;
        ((GlobalClass)getActivity().getApplicationContext()).listener = this;
        freezeButton = (Button) view.findViewById(R.id.button_freeze);
        temporaryFreezeButton = (Button) view.findViewById(R.id.button_freeze_temp);
        mySwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("Refresh", "onRefresh called from SwipeRefreshLayout");
                        getListVehicleDetails();
                    }
                }
        );
        calibri = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "Calibri.ttf");
        freezeButton.setTypeface(calibri);
        freezeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GlobalClass)getActivity().getApplicationContext()).freezeStatus = !((GlobalClass)getActivity().getApplicationContext()).freezeStatus;
                ((GlobalClass)getActivity().getApplicationContext()).freezeServices(listener, (((GlobalClass) getActivity().getApplicationContext()).freezeStatus));
            }
        });

        temporaryFreezeButton.setTypeface(calibri);
        temporaryFreezeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Objects.equals(temporaryFreezeButton.getText().toString(), "FREEZE")) {
                    ((GlobalClass)getActivity().getApplicationContext()).tempFreezeServices(listener, true);
                } else {
                    ((GlobalClass)getActivity().getApplicationContext()).tempFreezeServices(listener, false);
                }
            }
        });
        GlobalClass context = ((GlobalClass)getActivity().getApplicationContext());
        if((context.pending.size() == 0) && (context.history.size() == 0)) {
            getListVehicleDetails();
        }
        setupListView(view);
    }

    private void getListVehicleDetails() {
        ((GlobalClass)getActivity().getApplicationContext()).getServicesDetails(this);
    }

    // MARK: List view related functions

    private void setupListView(View view) {
        listView = (ListView) view.findViewById(R.id.pending_services_listview);
        adapter = new PendingServiceAdapter();
        listView.setAdapter(adapter);
    }

    // After the data is received from server, reloading the list view.

    @Override
    public void RetrievedServices() {
        if(mySwipeRefreshLayout.isRefreshing()) {
            mySwipeRefreshLayout.setRefreshing(false);
        }
        if(((GlobalClass)getActivity().getApplicationContext()).freezeStatus) {
            freezeButton.setText("UN-FREEZE ALL");
        } else{
            freezeButton.setText("FREEZE ALL");
        }
        if(((GlobalClass)getActivity().getApplicationContext()).endTime == 0 || ((GlobalClass)getActivity().getApplicationContext()).endTime < new Date().getTime()) {
            ((GlobalClass)getActivity().getApplicationContext()).stopTempFreezeTimer();
            temporaryFreezeButton.setText("FREEZE");
        } else {
            ((GlobalClass)getActivity().getApplicationContext()).startTempFreezeTimer();
            temporaryFreezeButton.setText("UN-FREEZE");
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

    private class PendingServiceAdapter extends BaseSwipeAdapter{

        @Override
        public int getCount() {
            if((((GlobalClass)getActivity().getApplicationContext()).pending.size())>0)
                return (((GlobalClass)getActivity().getApplicationContext()).pending.size());
            else
                return 0;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int pos) {
            return pos;
        }

        @Override
        public int getSwipeLayoutResourceId(int pos) {
            return R.id.swipe;
        }

        @Override
        public View generateView(final int pos, ViewGroup viewGroup) {
            View view = null ;
            view = getLayoutInflater(null).inflate(R.layout.pending_service_info_row, null);
            final SwipeLayout swipeLayout = (SwipeLayout) view.findViewById(getSwipeLayoutResourceId(pos));
            swipeLayout.addSwipeListener(new SimpleSwipeListener() {
                @Override
                public void onOpen(SwipeLayout layout) {
                    YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
                }
            });
            view.findViewById(R.id.trash).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity().getApplicationContext(), String.valueOf(pos), Toast.LENGTH_SHORT).show();
                    //call backend to delete item
                    //add intent here
                    swipeLayout.close();
                    Intent intent = new Intent(getActivity().getApplicationContext(), DismissService.class);
                    intent.putExtra("position", pos);
                    startActivity(intent);
                }
            });
            return view;
        }

        @Override
        public void fillValues(final int pos, View view) {

            final int i = pos;
            VehicleDetails service = ((GlobalClass)getActivity().getApplicationContext()).pending.get(i);
            Log.d(TAG, "code: " + service.code + " status:" + service.serviceStatus.toString());
            SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy(kk:mm)");
            String date = fmt.format(service.date_slot);

            final ConstraintLayout verifyLayout = (ConstraintLayout) view.findViewById(R.id.VerifyTab);
            final Button verifyButton = (Button) view.findViewById(R.id.ButtonVerifyCode);
            final Button cancel = (Button) view.findViewById(R.id.ButtonCancel);
            final Button verify = (Button) view.findViewById(R.id.ButtonContinue);
            final EditText editCode = (EditText) view.findViewById(R.id.code);
            final TextView registrationNumber = (TextView) view.findViewById(R.id.vehiclenumber);
            final TextView username = (TextView) view.findViewById(R.id.username);
            final TextView code = (TextView) view.findViewById(R.id.Code);
            final TextView dateSlot = (TextView) view.findViewById(R.id.date_slot);
            final ImageView vehicleImage = (ImageView) view.findViewById(R.id.Vehicle);
            final LinearLayout statusBar = (LinearLayout) view.findViewById(R.id.StatusBar);
            final Button started = (Button) view.findViewById(R.id.Started);
            final Button inProgress = (Button) view.findViewById(R.id.InProgress);
            final Button finalizing = (Button) view.findViewById(R.id.Finalizing);
            final Button done = (Button) view.findViewById(R.id.Done);

            verifyButton.setTypeface(calibri);
            editCode.setTypeface(calibri);

            registrationNumber.setTypeface(calibri);
            username.setTypeface(calibri, BOLD);
            code.setTypeface(calibri, BOLD);
            dateSlot.setTypeface(calibri);

            started.setTextSize(12);
            inProgress.setTextSize(12);
            finalizing.setTextSize(12);
            done.setTextSize(12);
            code.setTextSize(25);

            if(service.serviceStatus == ServiceStatus.NOT_VERIFIED) {
                verifyLayout.setVisibility(View.VISIBLE);
                statusBar.setVisibility(View.INVISIBLE);
                code.setVisibility(View.INVISIBLE);
            } else {
                verifyLayout.setVisibility(View.INVISIBLE);
                statusBar.setVisibility(View.VISIBLE);
                code.setVisibility(View.VISIBLE);
            }

            registrationNumber.setText(service.vehicleRegistrationNumber);
            username.setText(service.customername);

            dateSlot.setText(date);
            code.setText("CODE: ");
            code.append(service.code);

            int position = 0;
            for(; position < ((GlobalClass)getActivity().getApplicationContext()).vehicles.size(); position++) {
                if(((GlobalClass)getActivity().getApplicationContext()).vehicles.get(position).id == service.model_id) {
                    break;
                }
            }

            byte[] image = ((GlobalClass)getActivity().getApplicationContext()).vehicles.get(position).image;
            Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
            vehicleImage.setImageBitmap(bmp);

            switch (service.serviceStatus) {
                case DONE:
                    done.setBackground(getActivity().getApplicationContext().getDrawable(R.color.green));
                    done.setClickable(false);

                case FINALIZING:
                    finalizing.setBackground(getActivity().getApplicationContext().getDrawable(R.drawable.border_green));
                    finalizing.setClickable(false);

                case IN_PROGRESS:
                    inProgress.setBackground(getActivity().getApplicationContext().getDrawable(R.drawable.border_green));
                    inProgress.setClickable(false);

                case STARTED:
                    started.setBackground(getActivity().getApplicationContext().getDrawable(R.drawable.border_green));
                    started.setClickable(false);
            }

            verifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    verifyButton.setVisibility(View.INVISIBLE);
                    cancel.setVisibility(View.VISIBLE);
                    verify.setVisibility(View.VISIBLE);
                    editCode.setText("");
                    editCode.setVisibility(View.VISIBLE);
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    verifyButton.setVisibility(View.VISIBLE);
                    cancel.setVisibility(View.INVISIBLE);
                    verify.setVisibility(View.INVISIBLE);
                    editCode.setVisibility(View.INVISIBLE);
                }
            });

            verify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VehicleDetails service = ((GlobalClass) getActivity().getApplicationContext()).pending.get(i);
                    if (editCode.getText().toString().trim().equals(service.code)) {
                        service.serviceStatus = ServiceStatus.VERIFIED;
                        ((GlobalClass) getActivity().getApplicationContext()).pending.set((i), service);
                        ((GlobalClass) getActivity().getApplicationContext()).setServicesStatus(service);
                        cancel.performClick();
                        adapter.notifyDataSetChanged();
                    } else {
                        editCode.setText("Failed");
                    }
                }
            });

            started.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VehicleDetails service = ((GlobalClass) getActivity().getApplicationContext()).pending.get(i);
                    service.serviceStatus = ServiceStatus.STARTED;
                    ((GlobalClass) getActivity().getApplicationContext()).pending.set(i, service);
                    ((GlobalClass) getActivity().getApplicationContext()).setServicesStatus(service);
                    adapter.notifyDataSetChanged();
                }
            });

            inProgress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VehicleDetails service = ((GlobalClass) getActivity().getApplicationContext()).pending.get(i);
                    service.serviceStatus = ServiceStatus.IN_PROGRESS;
                    ((GlobalClass) getActivity().getApplicationContext()).pending.set(i, service);
                    ((GlobalClass) getActivity().getApplicationContext()).setServicesStatus(service);
                    adapter.notifyDataSetChanged();
                }
            });

            finalizing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VehicleDetails service = ((GlobalClass) getActivity().getApplicationContext()).pending.get(i);
                    service.serviceStatus = ServiceStatus.FINALIZING;
                    ((GlobalClass) getActivity().getApplicationContext()).pending.set(i, service);
                    ((GlobalClass) getActivity().getApplicationContext()).setServicesStatus(service);
                    adapter.notifyDataSetChanged();
                }
            });

            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VehicleDetails service = ((GlobalClass) getActivity().getApplicationContext()).pending.get(i);
                    service.serviceStatus = ServiceStatus.DONE;
                    //((GlobalClass) getActivity().getApplicationContext()).pending.set(i, service);
                    // Removing from pending list and adding to history list
                    ((GlobalClass) getActivity().getApplicationContext()).history.add(0, service);
                    ((GlobalClass) getActivity().getApplicationContext()).pending.remove(i);
                    ((GlobalClass) getActivity().getApplicationContext()).setServicesStatus(service);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }
}