package com.wheelcare.wheelcare;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.graphics.Typeface.BOLD;

/**
 * Created by Vimal on 15-11-2017.
 */

public class UserHomepage extends Fragment {

    private Typeface calibri;

    private static final String TAG = ServiceInfo.class.getSimpleName();
    private static final String URL = "http://" + GlobalClass.IPAddress + GlobalClass.Path + "cancelBookingService";

    InfoAdapter serviceProviderInfoAdapter;

    private int ServiceProviderID;
    private String RegistrationNumber = "KA 04 JD 1234";
    private String Name = "Service Provider Name";
    private String Address = "One two three four five six seven eight nine ten eleven twelve";
    private String PhoneNumber = "9743722774";
    private String Website = "serviceprovider.com";
    private ServiceType serviceType;
    private ServiceStatus serviceStatus = ServiceStatus.FINALIZING;

    private String Manufacturer = "XYZ";
    private String Model = "abc";
    private String Type = "Fuel";

    String date, boldCode;
    int index = 0;
    Context context;
    Button serviceButton;

    ArrayList<Vehicle> vehicles = new ArrayList<>();

    private int currentPosition = 0;

    // MARK: Initialization

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity().getApplicationContext();
        vehicles = ((GlobalClass)context).vehicles;
        calibri = Typeface.createFromAsset(context.getAssets(), "Calibri.ttf");
        setupListView(view);
        serviceButton = (Button) view.findViewById(R.id.proceedToService);

        serviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedToService(v);
            }
        });

    }

    // MARK: Setup List View

    private void setupListView(View view) {
        ListView listView = (ListView) view.findViewById(R.id.listView);
        serviceProviderInfoAdapter = new InfoAdapter();
        listView.setAdapter(serviceProviderInfoAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        vehicles = ((GlobalClass)context).vehicles;
        if(vehicles.size() > 0) {
            Vehicle v = vehicles.get(currentPosition);
            if (v.serviceStatus != null) {
                RegistrationNumber = v.registration_number;
                serviceStatus = v.serviceStatus;
                date = v.getSlot();
                boldCode = v.code;
                serviceType = v.serviceType;
                Name = v.companyName;
                Address = v.address;
                PhoneNumber = v.contactNumber;
                Website = v.website;
                serviceButton.setText("CANCEL BOOKING");
            } else {
                RegistrationNumber = v.registration_number;
                Manufacturer = v.manufacturer;
                Model = v.model;
                Type = v.type;
                serviceButton.setText("BOOK A SERVICE");
            }
            serviceProviderInfoAdapter.notifyDataSetChanged();
        }
    }

    private class InfoAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if(((GlobalClass)context).vehicles.size() > 0) {
                if (vehicles.get(currentPosition).serviceStatus != null) {
                    return 5;
                } else {
                    return 2;
                }
            } else {
                return 1;
            }
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

            if(((GlobalClass)context).vehicles.size() == 0) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.no_cars, null);
                convertView.setMinimumHeight(parent.getMeasuredHeight());
                ConstraintLayout layout = (ConstraintLayout)convertView.findViewById(R.id.backLayout);
                layout.setMinHeight(parent.getMeasuredHeight());
                serviceButton.setText("ADD NEW CAR");
            } else {
                if (position == 0) {

                    final CarouselView serviceProviderImage;
                    final TextView registrationNumber;

                    if (((GlobalClass) context).vehicles.get(currentPosition).serviceStatus != null) {
                        convertView = getActivity().getLayoutInflater().inflate(R.layout.service_status_info, null);
                        serviceProviderImage = (CarouselView) convertView.findViewById(R.id.CarImage);
                        registrationNumber = (TextView) convertView.findViewById(R.id.RegistrationNumber);
                    } else {
                        convertView = getActivity().getLayoutInflater().inflate(R.layout.service_provider_state, null);
                        serviceProviderImage = (CarouselView) convertView.findViewById(R.id.vehImage);
                        registrationNumber = (TextView) convertView.findViewById(R.id.Number);
                    }

                    Log.d("INSIDE CAR SIZE", String.valueOf(vehicles.size()));
                    serviceProviderImage.setPageCount(vehicles.size());

                    serviceProviderImage.setViewListener(new ViewListener() {
                        @Override
                        public View setViewForPosition(int position) {
                            View view = getActivity().getLayoutInflater().inflate(R.layout.car_view, null);
                            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
                            Bitmap bmp = BitmapFactory.decodeByteArray(((GlobalClass) getActivity().getApplicationContext()).vehicles.get(position).image, 0, ((GlobalClass) getActivity().getApplicationContext()).vehicles.get(position).image.length);
                            imageView.setImageBitmap(bmp);
                            return view;
                        }
                    });

                    registrationNumber.setText(vehicles.get(currentPosition).registration_number);

                    serviceProviderImage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                            Log.d("Scroll", "Position: " + position + "offset: " + positionOffset + "px: " + positionOffsetPixels);
                            if(positionOffset == 0) {
                                currentPosition = position;
                                Vehicle v = ((GlobalClass) getActivity().getApplicationContext()).vehicles.get(position);
                                registrationNumber.setText(v.registration_number);
                                if (((GlobalClass) context).vehicles.get(position).serviceStatus != null) {
                                    RegistrationNumber = v.registration_number;
                                    serviceStatus = v.serviceStatus;
                                    date = v.getSlot();
                                    boldCode = v.code;
                                    serviceType = v.serviceType;
                                    Name = v.companyName;
                                    Address = v.address;
                                    PhoneNumber = v.contactNumber;
                                    Website = v.website;
                                    serviceButton.setText("CANCEL BOOKING");
                                } else {
                                    RegistrationNumber = v.registration_number;
                                    Manufacturer = v.manufacturer;
                                    Model = v.model;
                                    Type = v.type;
                                    serviceButton.setText("BOOK A SERVICE");
                                }
                                notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onPageSelected(int position) {}

                        @Override
                        public void onPageScrollStateChanged(int state) {}
                    });

                    serviceProviderImage.setCurrentItem(currentPosition);

                    // TODO

                    if (((GlobalClass) context).vehicles.get(currentPosition).serviceStatus != null) {

                        final TextView service = (TextView) convertView.findViewById(R.id.serviceType);
                        final TextView code = (TextView) convertView.findViewById(R.id.Code);
                        final TextView codeVerified = (TextView) convertView.findViewById(R.id.CodeVerified);
                        final TextView dateSlot = (TextView) convertView.findViewById(R.id.dateSlot);
                        final TextView started = (TextView) convertView.findViewById(R.id.Started);
                        final TextView inProgress = (TextView) convertView.findViewById(R.id.InProgress);
                        final TextView finalizing = (TextView) convertView.findViewById(R.id.Finalizing);
                        final TextView done = (TextView) convertView.findViewById(R.id.Done);

                        serviceButton.setText("CANCEL BOOKING");
                        registrationNumber.setTypeface(calibri);
                        codeVerified.setTypeface(calibri);
                        service.setTypeface(calibri);
                        code.setTypeface(calibri, BOLD);
                        dateSlot.setTypeface(calibri);

                        started.setTextSize(12);
                        inProgress.setTextSize(12);
                        finalizing.setTextSize(12);
                        done.setTextSize(12);
                        code.setTextSize(25);

                        dateSlot.setText(date);

                        code.setText("Code: " + boldCode);

                        if (serviceType == ServiceType.THREE_D) {
                            service.setText("3D Service");
                        } else {
                            service.setText("Manual Service");
                        }

                        switch (serviceStatus) {
                            case DONE:
                                done.setBackground(getActivity().getDrawable(R.color.green));
                                done.setClickable(false);

                            case FINALIZING:
                                finalizing.setBackground(getActivity().getDrawable(R.drawable.border_green));
                                finalizing.setClickable(false);

                            case IN_PROGRESS:
                                inProgress.setBackground(getActivity().getDrawable(R.drawable.border_green));
                                inProgress.setClickable(false);

                            case STARTED:
                                started.setBackground(getActivity().getDrawable(R.drawable.border_green));
                                started.setClickable(false);

                            case VERIFIED:
                                codeVerified.setText("Code Verified");
                                break;

                            default:
                                codeVerified.setText("Code Not Verified");
                        }
                    } else {

                        TextView selectedCar = (TextView) convertView.findViewById(R.id.Name);
                        TextView ManufacturerLabel = (TextView) convertView.findViewById(R.id.Manufacturer);
                        TextView ManufacturerName = (TextView) convertView.findViewById(R.id.ManuName);
                        TextView ModelLabel = (TextView) convertView.findViewById(R.id.Model);
                        TextView ModelName = (TextView) convertView.findViewById(R.id.ModelName);
                        TextView TypeLabel = (TextView) convertView.findViewById(R.id.Type);
                        TextView TypeName = (TextView) convertView.findViewById(R.id.TypeName);

                        selectedCar.setTypeface(calibri);
                        ManufacturerLabel.setTypeface(calibri);
                        ManufacturerName.setTypeface(calibri);
                        ModelLabel.setTypeface(calibri);
                        ModelName.setTypeface(calibri);
                        TypeLabel.setTypeface(calibri);
                        TypeName.setTypeface(calibri);
                        registrationNumber.setTypeface(calibri);

                        registrationNumber.setTextColor(Color.BLACK);
                        ManufacturerName.setTextColor(Color.BLACK);
                        ModelName.setTextColor(Color.BLACK);
                        TypeName.setTextColor(Color.BLACK);

                        registrationNumber.setTextSize(20);
                        ManufacturerName.setTextSize(20);
                        ModelName.setTextSize(20);
                        TypeName.setTextSize(20);

                        serviceButton.setText("BOOK A SERVICE");
                        selectedCar.setText("Selected Car");
                        ManufacturerLabel.setText("Manufacturer");
                        ManufacturerName.setText(Manufacturer);
                        ModelLabel.setText("Model");
                        ModelName.setText(Model);
                        TypeLabel.setText("Engine Type");
                        TypeName.setText(Type);
                    }

                } else if (position == 1) {
                    if(vehicles.get(currentPosition).serviceStatus != null) {
                        convertView = getActivity().getLayoutInflater().inflate(R.layout.payment_code, null);
                        TextView serviceProviderName = (TextView) convertView.findViewById(R.id.Key);
                        TextView makeEmpty = (TextView) convertView.findViewById(R.id.Value);
                        makeEmpty.setVisibility(View.INVISIBLE);

                        serviceProviderName.setTypeface(calibri);
                        serviceProviderName.setText(Name);
                    } else {
                        convertView = getActivity().getLayoutInflater().inflate(R.layout.offers_view, null);
                        GridView gridView = (GridView) convertView.findViewById(R.id.offersView);
                    }
                } else {
                    if (vehicles.get(currentPosition).serviceStatus != null) {
                        convertView = getActivity().getLayoutInflater().inflate(R.layout.service_provider_details, null);
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
                                if (lines == 2) {
                                    detailTypeText.setHeight(60);
                                } else if (lines == 3) {
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
                                convertView = new View(getActivity().getApplicationContext());
                                convertView.setMinimumHeight(5);
                                convertView.setBackgroundResource(R.color.white);
                        }
                    } else {

                    }
                }
            }
            return convertView;
        }
    }

    // MARK: Setup Service Button

    private void setupServiceButton(View view) {
        serviceButton.setText("CANCEL");
        serviceButton.setTypeface(calibri);
        if(serviceStatus != ServiceStatus.NOT_VERIFIED) {

        }
    }

    // MARK: Button Pressed

    private void changeState() {

    }

    public void proceedToService(View view) {
        if (vehicles.size() == 0) {
            Intent i = new Intent(getActivity().getApplicationContext(), CarRegistration.class);
            i.putExtra("MyCars", true);
            startActivity(i);
        } else if (vehicles.get(currentPosition).serviceStatus == null) {
            Intent i = new Intent(getActivity().getApplicationContext(), MapsActivity.class);
            i.putExtra("vehicleIndex", currentPosition);
            startActivity(i);
        } else {

            if (!(((GlobalClass) getActivity().getApplicationContext()).isInternetAvailable())) {
                return;
            }
            final JSONObject object = new JSONObject();
            try {
                object.put("userId", AuthenticationManager.getInstance().getUserID());
                object.put("reg_no", RegistrationNumber);
                object.put("service_status", "not_verified");
                if (((GlobalClass) context).vehicles.get(currentPosition).validity == 0 || ((GlobalClass) context).vehicles.get(currentPosition).validity < new Date().getTime()) {
                    ((GlobalClass) context).vehicles.get(currentPosition).validity = (new Date().getTime() + (86400 * 1000 * 3));
                    object.put("validity", ((GlobalClass) context).vehicles.get(currentPosition).validity);
                } else {
                    object.put("validity", ((GlobalClass) context).vehicles.get(currentPosition).validity);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (object == null) return;

            WebServiceManager.getInstance(getActivity().getApplicationContext()).addToRequestQueue(
                    // Request a string response from the provided URL.
                    new JsonObjectRequest(Request.Method.POST, URL, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    ((GlobalClass) context).userCarLists.remove(index);
                                    changeState();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d(TAG, error.toString());
                                }
                            }
                    ) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> header = new HashMap<>();
                            header.put("X-ACCESS-TOKEN", AuthenticationManager.getInstance().getAccessToken());
                            Log.e(TAG, "header " + header);
                            return header;
                        }

                        @Override
                        public String getBodyContentType() {
                            return "application/json; charset=utf-8";
                        }

                        @Override
                        public byte[] getBody() {
                            try {
                                return object == null ? null : object.toString().getBytes("utf-8");
                            } catch (UnsupportedEncodingException uee) {
                                VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", object.toString(), "utf-8");
                                return null;
                            }
                        }

                        @Override
                        protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                            Log.d(TAG, "StatusCode: " + String.valueOf(response.statusCode));
                            try {
                                String jsonString = new String(response.data,
                                        HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));

                                JSONObject result = null;

                                if (jsonString != null && jsonString.length() > 0)
                                    result = new JSONObject(jsonString);

                                return Response.success(result,
                                        HttpHeaderParser.parseCacheHeaders(response));
                            } catch (UnsupportedEncodingException e) {
                                return Response.error(new ParseError(e));
                            } catch (JSONException je) {
                                return Response.error(new ParseError(je));
                            }
                        }
                    }
            );
        }
    }
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

}
