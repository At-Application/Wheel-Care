package com.wheelcare.wheelcare;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.wheelcare.wheelcare.R;
import com.google.android.gms.location.LocationListener;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.graphics.Typeface.BOLD;


public class UserHome extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = UserHome.class.getSimpleName();

    private static final String ServiceProviderListURL = "http://" + GlobalClass.IPAddress + "/wheelcare/rest/consumer/getSPInfos";

    private static final String SUCCESS = "200";

    private int selectedLocation;

    private Typeface calibri;
    String Distance;
    Context context;
    GoogleMap googlemap;
    MapView mapview;
    View view;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    TextView serviceProvider, distance, regNo, boldCode, codeVerified, slot;
    Button started, inProgress, finalizing, done;
    ConstraintLayout infoView, progressBar;

    UserCarList userCarListObj;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        view = inflater.inflate(R.layout.fragment_home, container, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity().getApplicationContext();
        calibri = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "Calibri.ttf");
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            checkLocationPermission();
//        }
        //you can set the title for your toolbar here for different fragments different titles
        mapview = (MapView) view.findViewById(R.id.map);
        infoView = (ConstraintLayout) view.findViewById(R.id.spInfoLayout);
        serviceProvider = (TextView) view.findViewById(R.id.serviceProviderName);
        distance = (TextView) view.findViewById(R.id.providerDistance);
        serviceProvider.setTypeface(calibri);
        distance.setTypeface(calibri);
        infoView.setVisibility(View.INVISIBLE);
        infoView.setClickable(false);

        progressBar = (ConstraintLayout) view.findViewById(R.id.ProgressBar);
        regNo = (TextView) progressBar.findViewById(R.id.textView2);
        boldCode = (TextView) progressBar.findViewById(R.id.textView3);
        slot = (TextView) progressBar.findViewById(R.id.textView4);
        codeVerified = (TextView) progressBar.findViewById(R.id.textView5);
        started = (Button) progressBar.findViewById(R.id.Started);
        inProgress = (Button) progressBar.findViewById(R.id.InProgress);
        finalizing = (Button) progressBar.findViewById(R.id.Finalizing);
        done = (Button) progressBar.findViewById(R.id.Done);
        regNo.setTypeface(calibri);
        boldCode.setTypeface(calibri, BOLD);
        slot.setTypeface(calibri);
        codeVerified.setTypeface(calibri);
        started.setTypeface(calibri, BOLD);
        inProgress.setTypeface(calibri, BOLD);
        finalizing.setTypeface(calibri, BOLD);
        done.setTypeface(calibri, BOLD);
        progressBar.setVisibility(View.INVISIBLE);
        progressBar.setClickable(false);

        if (mapview != null) {
            mapview.onCreate(null);
            mapview.onResume();
            mapview.getMapAsync(this);
        }

        infoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the next screen
                startActivity(new Intent(getActivity().getApplicationContext(), ServiceProviderInfo.class).putExtra("index", selectedLocation).putExtra("distance", Distance));
            }
        });

        progressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity().getApplicationContext(), ServiceInfo.class).putExtra("UserCarListIndex", ((GlobalClass)context).userCarLists.indexOf(userCarListObj)));
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        MapsInitializer.initialize(getContext());
        googlemap = googleMap;
        //googlemap.setMyLocationEnabled(true);
        googlemap.getUiSettings().setMyLocationButtonEnabled(true);
        googlemap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker m) {
                boolean proceed = true;
                if (((GlobalClass) context).userCarLists.size() > 0) {
                    for (UserCarList obj : ((GlobalClass) context).userCarLists) {
                        if (obj.getServiceStatus() != null) {
                            switch (obj.getServiceStatus()) {
                                case NOT_VERIFIED:
                                case VERIFIED:
                                case STARTED:
                                case IN_PROGRESS:
                                case FINALIZING:
                                    progressBar.setVisibility(View.VISIBLE);
                                    infoView.setClickable(false);
                                    proceed = false;
                                    break;

                                case DONE:
                                case DISMISS:
                                    progressBar.setVisibility(View.INVISIBLE);
                                    proceed = true;
                                    break;
                            }
                            break;
                        } else {
                            proceed = true;
                        }
                    }
                }
                if (proceed) {
                    Location newLocation = new Location("newlocation");
                    newLocation.setLatitude(m.getPosition().latitude);
                    newLocation.setLongitude(m.getPosition().longitude);
                    infoView.setVisibility(View.VISIBLE);
                    infoView.setClickable(true);
                    progressBar.setVisibility(View.INVISIBLE);
                    Distance = String.format("%.1f", mLastLocation.distanceTo(newLocation) / 1000);
                    //Toast.makeText(MapsActivity.this, Float.toString(mLastLocation.distanceTo(newLocation)/1000), Toast.LENGTH_LONG).show();
                    distance.setText(Distance);
                    if (Distance.contentEquals("1.0") || Distance.contentEquals("1")) {
                        distance.append(" km");
                    } else {
                        distance.append(" kms");
                    }


                    LatLng loc = new LatLng(m.getPosition().latitude, m.getPosition().longitude);
                    for (ServiceProviderDetails obj : ((GlobalClass) context).serviceProviders) {
                        if (loc.latitude == obj.getLocation().latitude && loc.longitude == obj.getLocation().longitude) {
                            serviceProvider.setText(obj.getCompanyName());
                            selectedLocation = ((GlobalClass) context).serviceProviders.indexOf(obj);
                            break;
                        }
                    }
                }
                //   providerName.setText(Float.toString(mLastLocation.distanceTo(newLocation)/1000));
                return true;
            }
        });

        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (mLastLocation != null) {
                    getServiceProviders();
                }
                return false;
            }
        });

        // Setting a click event handler for the map
        googlemap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                infoView.setVisibility(View.INVISIBLE);
                infoView.setClickable(false);
                for (UserCarList obj : ((GlobalClass) context).userCarLists) {
                    if (obj.getServiceStatus() != null) {
                        switch (obj.getServiceStatus()) {
                            case NOT_VERIFIED:
                            case VERIFIED:
                            case STARTED:
                            case IN_PROGRESS:
                            case FINALIZING:
                                progressBar.setVisibility(View.VISIBLE);
                                break;

                            case DONE:
                            case DISMISS:
                                progressBar.setVisibility(View.INVISIBLE);
                                break;
                        }
                        break;
                    }
                }
            }
        });
        googlemap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                googlemap.setMyLocationEnabled(true);
                googlemap.getUiSettings().setMyLocationButtonEnabled(true);

            }
        } else {
            buildGoogleApiClient();
            googlemap.setMyLocationEnabled(true);
            googlemap.getUiSettings().setMyLocationButtonEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));


        //move map camera
        googlemap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googlemap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googlemap.animateCamera(CameraUpdateFactory.zoomTo(11));


        //googlemap.addMarker(markerOptions);

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

        if (mLastLocation != null) {
            getServiceProviders();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

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

    public BitmapDescriptor getMarkerIcon(String color) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

    // MARK: For setting the status

    public void getServiceProviders() {
        if(!this.isVisible()) return;
        JSONObject object = createJSONObject();
        if (object != null) {
            ServiceProviderCall(object);
        } else {
            Log.e(TAG, "Failed to create JSON object for fetching service provider info");
        }
    }

    public JSONObject createJSONObject() {
        JSONObject object = new JSONObject();
        try {
            object.put("userId", AuthenticationManager.getInstance().getUserID());
            object.put("lat", String.valueOf(mLastLocation.getLatitude()));
            object.put("lng", String.valueOf(mLastLocation.getLongitude()));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return object;
    }

    private void ServiceProviderCall(final JSONObject object) {
        // Add the request to the RequestQueue.
        WebServiceManager.getInstance(getActivity().getApplicationContext()).addToRequestQueue(
                // Request a string response from the provided URL.
                new JsonObjectRequest(Request.Method.POST, ServiceProviderListURL, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, response.toString());
                                try {
                                    // Actual data received here
                                    populateUserCarData((JSONArray) response.get("userCarSPInfo"));
                                    populateServiceProviders((JSONArray) response.get("spInfos"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
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
                        return super.parseNetworkResponse(response);
                    }
                }
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        UserCarList lowest = new UserCarList();
        lowest.setSlotValue(0);
        for (UserCarList obj : ((GlobalClass) context).userCarLists) {
            if (obj.getServiceStatus() != null) {
                switch (obj.getServiceStatus()) {
                    case FINALIZING:
                    case IN_PROGRESS:
                    case STARTED:
                    case VERIFIED:
                    case NOT_VERIFIED:
                        if (lowest.getSlotValue() == 0 || obj.getSlotValue() < lowest.getSlotValue()) {
                            lowest = obj;
                            userCarListObj = lowest;
                        }
                        break;

                    case DONE:
                    case DISMISS:
                        break;
                }
            }
        }
        if (lowest.getSlotValue() == 0) {
            progressBar.setVisibility(View.INVISIBLE);
            infoView.setVisibility(infoView.getVisibility());
            infoView.setClickable(infoView.getVisibility() == View.VISIBLE);
        } else {
            if (lowest.getServiceStatus() != null) {
                switch (lowest.getServiceStatus()) {
                    case FINALIZING:
                        finalizing.setBackground(getActivity().getDrawable(R.drawable.border_green));
                    case IN_PROGRESS:
                        inProgress.setBackground(getActivity().getDrawable(R.drawable.border_green));
                    case STARTED:
                        started.setBackground(getActivity().getDrawable(R.drawable.border_green));
                    case VERIFIED:
                        codeVerified.setText("Code Verified");
                    case NOT_VERIFIED:
                        if (lowest.getServiceStatus() == ServiceStatus.NOT_VERIFIED)
                            codeVerified.setText("Code Not Verified");
                        regNo.setText(lowest.getRegistrationNumber());
                        boldCode.setText("CODE: " + lowest.getCode());
                        slot.setText(lowest.getSlot());
                        progressBar.setVisibility(View.VISIBLE);
                        infoView.setVisibility(View.INVISIBLE);
                        infoView.setClickable(false);
                        break;

                    case DONE:
                    case DISMISS:
                        progressBar.setVisibility(View.INVISIBLE);
                        infoView.setVisibility(infoView.getVisibility());
                        infoView.setClickable(infoView.getVisibility() == View.VISIBLE);
                        break;
                }
            }
        }
    }

    private void populateUserCarData(JSONArray response) {
        if (response.length() > 0) {
            try {
                ArrayList<UserCarList> list = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    JSONObject obj = response.getJSONObject(i);
                    UserCarList car = new UserCarList(obj);
                    list.add(car);
                }
                ((GlobalClass) context).userCarLists = list;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        UserCarList lowest = new UserCarList();
        lowest.setSlotValue(0);
        for (UserCarList obj : ((GlobalClass) context).userCarLists) {
            if (obj.getServiceStatus() != null) {
                switch (obj.getServiceStatus()) {
                    case FINALIZING:
                    case IN_PROGRESS:
                    case STARTED:
                    case VERIFIED:
                    case NOT_VERIFIED:
                        if (lowest.getSlotValue() == 0 || obj.getSlotValue() < lowest.getSlotValue()) {
                            lowest = obj;
                            userCarListObj = lowest;
                        }
                        break;

                    case DONE:
                    case DISMISS:
                        break;
                }
            }
        }
        if (lowest.getSlotValue() == 0) {
            progressBar.setVisibility(View.INVISIBLE);
            infoView.setVisibility(infoView.getVisibility());
            infoView.setClickable(infoView.getVisibility() == View.VISIBLE);
        } else {
            if (lowest.getServiceStatus() != null) {
                switch (lowest.getServiceStatus()) {
                    case FINALIZING:
                        finalizing.setBackground(getActivity().getDrawable(R.drawable.border_green));
                    case IN_PROGRESS:
                        inProgress.setBackground(getActivity().getDrawable(R.drawable.border_green));
                    case STARTED:
                        started.setBackground(getActivity().getDrawable(R.drawable.border_green));
                    case VERIFIED:
                        codeVerified.setText("Code Verified");
                    case NOT_VERIFIED:
                        if (lowest.getServiceStatus() == ServiceStatus.NOT_VERIFIED)
                            codeVerified.setText("Code Not Verified");
                        regNo.setText(lowest.getRegistrationNumber());
                        boldCode.setText("CODE: " + lowest.getCode());
                        slot.setText(lowest.getSlot());
                        progressBar.setVisibility(View.VISIBLE);
                        infoView.setVisibility(View.INVISIBLE);
                        infoView.setClickable(false);
                        break;

                    case DONE:
                    case DISMISS:
                        progressBar.setVisibility(View.INVISIBLE);
                        infoView.setVisibility(infoView.getVisibility());
                        infoView.setClickable(infoView.getVisibility() == View.VISIBLE);
                        break;
                }
            }
        }
    }

    private void populateServiceProviders(JSONArray response) {

        if(response.length() > 0) {
            try {
                ArrayList<ServiceProviderDetails> sp = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    JSONObject obj = response.getJSONObject(i);

                    ServiceProviderDetails spDetails = new ServiceProviderDetails(obj);
                    sp.add(spDetails);
                }
                ((GlobalClass)context).serviceProviders = sp;
                for (int i = 0; i < ((GlobalClass)context).serviceProviders.size(); i++) {
                    // Creating a marker
                    ServiceProviderDetails obj = ((GlobalClass)context).serviceProviders.get(i);
                    addMaker(obj.getLocation());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void addMaker(LatLng loc) {
        MarkerOptions markerOptions = new MarkerOptions();
        // Setting the position for the marker
        markerOptions.position(loc);
        // Setting the title for the marker.
        // This will be displayed on taping the marker
        //markerOptions.title(String.valueOf(loc.latitude) + ":" + String.valueOf(loc.latitude));
        markerOptions.icon(getMarkerIcon("#8f216f"));
        // Clears the previously touched position
        // Animating to the touched position
        //googlemap.animateCamera(CameraUpdateFactory.newLatLng(loc));
        // Placing a marker on the touched position
        googlemap.addMarker(markerOptions);
    }
}
