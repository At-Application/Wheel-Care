package com.wheelcare.wheelcare;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = UserHome.class.getSimpleName();

    private static final String ServiceProviderListURL = "http://" + GlobalClass.IPAddress + GlobalClass.Path + "getSPInfos";

    private static final String SUCCESS = "200";

    private int selectedLocation;

    private Typeface calibri;
    String Distance, serviceProviderName;
    Context context;
    GoogleMap googlemap;
    MapView mapview;
    View view;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    TextView serviceProvider;
    ConstraintLayout infoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        context = this.getApplicationContext();
        calibri = Typeface.createFromAsset(this.getAssets(), "Calibri.ttf");
        infoView = (ConstraintLayout) findViewById(R.id.spInfoLayout);
        serviceProvider = (TextView) findViewById(R.id.serviceProviderName);
        serviceProvider.setTypeface(calibri);
        infoView.setVisibility(View.VISIBLE);
        infoView.setClickable(false);

        serviceProvider.setText("Please click on the marker");

        if (mapview != null) {
            mapview.onCreate(null);
            mapview.onResume();
            mapview.getMapAsync(this);
        }

        infoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the next screen
                //startActivity(new Intent(MapsActivity.this, ServiceProviderInfo.class).putExtra("index", selectedLocation).putExtra("distance", Distance));
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        MapsInitializer.initialize(getApplicationContext());
        if(googlemap == null) {
            googlemap = googleMap;
            LatLng india = new LatLng(12.9716, 77.5946);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(india));
            googlemap.animateCamera(CameraUpdateFactory.newLatLng(india));
            googlemap.animateCamera(CameraUpdateFactory.zoomTo(11));
        }
        googlemap = googleMap;
        //googlemap.setMyLocationEnabled(true);

        googlemap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {

                Log.d("INFO Window", "Showing");

                // Getting view from the layout file info_window_layout
                View v = getLayoutInflater().inflate(R.layout.info_window_layout, null);

                // Getting the position from the marker
                LatLng latLng = arg0.getPosition();

                // Getting reference to the TextView to set latitude
                TextView name = (TextView) v.findViewById(R.id.tv_lat);

                // Getting reference to the TextView to set longitude
                TextView dist = (TextView) v.findViewById(R.id.tv_lng);

                Button b = (Button) v.findViewById(R.id.button2);

                // Setting the latitude
                name.setText("Name:" + serviceProviderName);

                // Setting the longitude
                dist.setText("Distance:"+ Distance);

                googlemap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    public void onInfoWindowClick(Marker marker) {
                        Intent i = new Intent(MapsActivity.this, ServiceProviderInfo.class).putExtra("index", selectedLocation).putExtra("distance", Distance);
                        int pos = getIntent().getExtras().getInt("vehicleIndex");
                        i.putExtra("vehicleIndex", pos);
                        startActivity(i);
                    }
                });
                        // Returning the view containing InfoWindow contents
                return v;

            }
        });

        googlemap.getUiSettings().setMyLocationButtonEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        googlemap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker m) {
                Location newLocation = new Location("newlocation");
                newLocation.setLatitude(m.getPosition().latitude);
                newLocation.setLongitude(m.getPosition().longitude);
                infoView.setVisibility(View.VISIBLE);
                infoView.setClickable(false);
                Distance = String.format("%.1f", mLastLocation.distanceTo(newLocation) / 1000);
                //Toast.makeText(MapsActivity.this, Float.toString(mLastLocation.distanceTo(newLocation)/1000), Toast.LENGTH_LONG).show();
                if (Distance.contentEquals("1.0") || Distance.contentEquals("1")) {
                    //distance.append(" km");
                    Distance = Distance + " km";
                } else {
                    //distance.append(" kms");
                    Distance = Distance + " kms";
                }

                if (((GlobalClass) context).serviceProviders != null) {
                    LatLng loc = new LatLng(m.getPosition().latitude, m.getPosition().longitude);
                    for (ServiceProviderDetails obj : ((GlobalClass) context).serviceProviders) {
                        if (loc.latitude == obj.getLocation().latitude && loc.longitude == obj.getLocation().longitude) {
                            //serviceProvider.setText(obj.getCompanyName());
                            serviceProviderName = obj.getCompanyName();
                            selectedLocation = ((GlobalClass) context).serviceProviders.indexOf(obj);
                            break;
                        }
                    }
                }
                m.showInfoWindow();
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
        googlemap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
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
        mGoogleApiClient = new GoogleApiClient.Builder(this)
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
        if (ContextCompat.checkSelfPermission(this,
                //Manifest.permission.ACCESS_COARSE_LOCATION)
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
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

        // Add Markers only for testing here
        LatLng sat = new LatLng(17.3005, 82.6119);
        addMaker(sat);

        LatLng sat1 = new LatLng(17.8803197,82.7872235);
        addMaker(sat1);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
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

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "ON START CALLED");
        if(mLastLocation != null) {
            getServiceProviders();
        }
    }

    public void getServiceProviders() {
        if(!(((GlobalClass)getApplicationContext()).isInternetAvailable())) {
            return;
        }
//        if(!this.isVisible()) return;
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
        WebServiceManager.getInstance(getApplicationContext()).addToRequestQueue(
                // Request a string response from the provided URL.
                new JsonObjectRequest(Request.Method.POST, ServiceProviderListURL, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, response.toString());
                                try {
                                    // Actual data received here
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
       Marker marker = googlemap.addMarker(markerOptions);
        //marker.showInfoWindow();
    }
}
