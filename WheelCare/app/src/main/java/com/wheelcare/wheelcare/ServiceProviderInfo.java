package com.wheelcare.wheelcare;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.wheelcare.wheelcare.R;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Vimal on 08-09-2017.
 */

public class ServiceProviderInfo extends RootActivity {

    private static final String TAG = ServiceProviderInfo.class.getSimpleName();
    private Typeface calibri;
    private int index;

    private int ServiceProviderID;
    private String Name = "Service Provider Name";
    private String Distance = "1.2";
    private String Address = "One two three four five six seven eight nine ten eleven twelve";
    private String PhoneNumber = "9743722774";
    private String Website = "serviceprovider.com";
    private ArrayList<String> ProviderImages = null;
    private int currentPosition = 0;
    // MARK: Initialization

    InfoAdapter serviceProviderInfoAdapter;

    private static final String ServiceProviderInfoURL = "http://" + GlobalClass.IPAddress + GlobalClass.Path + "spView";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_info);
        calibri = Typeface.createFromAsset(getApplicationContext().getAssets(), "Calibri.ttf");
        setupIntentData();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupToolbar();
        setupListView();
        setupServiceButton();
    }

    // MARK: Setup Intent Data

    private void setupIntentData() {
        Bundle extras = getIntent().getExtras();
        index = extras.getInt("index");
        Distance = extras.getString("distance");
        ServiceProviderDetails details = ((GlobalClass)getApplicationContext()).serviceProviders.get(index);
        ServiceProviderID = details.getId();
        Name = details.getCompanyName();
        Address = details.getAddress();
        PhoneNumber = details.getContactNumber();
        Website = details.getWebsite();
        ProviderImages = new ArrayList<>();
        getProviderImages();
    }

    // MARK: Setup Tool Bar

    private void setupToolbar() {
        TextView title = (TextView) findViewById(R.id.toolbar_title);
        title.setText("Provider Information");
    }

    // MARK: Setup List View

    private void setupListView() {
        ListView listView = (ListView) findViewById(R.id.listView);
        serviceProviderInfoAdapter = new InfoAdapter();
        listView.setAdapter(serviceProviderInfoAdapter);
    }

    private class InfoAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 5;
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
                convertView = getLayoutInflater().inflate(R.layout.service_provider_image, null);
                final CarouselView serviceProviderImage = (CarouselView) convertView.findViewById(R.id.serviceProviderImage);
                TextView serviceProviderName = (TextView) convertView.findViewById(R.id.serviceProviderName);
                TextView serviceProviderDistance = (TextView) convertView.findViewById(R.id.serviceProviderDistance);

                serviceProviderImage.setPageCount(ProviderImages != null ? ProviderImages.size() : 1);

                serviceProviderImage.setViewListener(new ViewListener() {
                    @Override
                    public View setViewForPosition(int position) {
                        View view = getLayoutInflater().inflate(R.layout.car_view, null);
                        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
                        if(ProviderImages.size() != 0) {
                            byte[] decodedString = Base64.decode(ProviderImages.get(position), Base64.DEFAULT);
                            imageView.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
                        } else {
                            imageView.setImageResource(R.drawable.image_view_placeholder);
                        }
                        return view;
                    }
                });

                serviceProviderImage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        currentPosition = position;
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });

                serviceProviderImage.setCurrentItem(currentPosition);


                serviceProviderName.setTypeface(calibri);
                serviceProviderDistance.setTypeface(calibri);

                serviceProviderName.setText(Name);
                serviceProviderDistance.setText(Distance);
                if (Distance.contentEquals("1.0") || Distance.contentEquals("1")) {
                    serviceProviderDistance.append(" km");
                } else {
                    serviceProviderDistance.append(" kms");
                }
            } else if(position == 4) {
                convertView = getLayoutInflater().inflate(R.layout.service_provider_services, null);
                ImageView detailTypeImage = (ImageView) convertView.findViewById(R.id.detailTypeImage);
                TextView servicesOffered = (TextView) convertView.findViewById(R.id.servicesOffered);
                TextView wheelAlignment = (TextView) convertView.findViewById(R.id.wheelAlginment);
                TextView wheelBalancing = (TextView) convertView.findViewById(R.id.wheelBalancing);

                detailTypeImage.setImageResource(R.drawable.settings);
                servicesOffered.setTypeface(calibri, Typeface.BOLD);
                wheelAlignment.setTypeface(calibri);
                wheelBalancing.setTypeface(calibri);

            } else {
                convertView = getLayoutInflater().inflate(R.layout.service_provider_details, null);
                ImageView detailTypeImage = (ImageView) convertView.findViewById(R.id.detailTypeImage);
                TextView detailTypeText = (TextView) convertView.findViewById(R.id.detailTypeText);

                detailTypeText.setTypeface(calibri);

                switch (position) {
                    case 1:
                        detailTypeImage.setImageResource(R.drawable.location);
                        detailTypeText.setMaxLines(3);
                        detailTypeText.setSingleLine(false);
                        detailTypeText.setHorizontallyScrolling(false);
                        int lines = detailTypeText.getLineCount();
                        if(lines == 2) {
                            detailTypeText.setHeight(60);
                        } else if(lines == 3) {
                            detailTypeText.setHeight(70);
                        }
                        detailTypeText.setText(Address);
                        break;

                    case 2:
                        detailTypeImage.setImageResource(R.drawable.leftcall);
                        detailTypeText.setText(PhoneNumber);
                        break;

                    case 3:
                        detailTypeImage.setImageResource(R.drawable.web);
                        detailTypeText.setText(Website);
                        break;
                }
            }
            return convertView;
        }
    }

    // MARK: Setup Service Button

    private void setupServiceButton() {
        Button serviceButton = (Button) findViewById(R.id.proceedToService);
        serviceButton.setTypeface(calibri);
    }

    // MARK: Button Pressed

    public void proceedToService(View view) {
        // Send information as Intent to the next screen
        // Service Provider ID, Current Date, Slots with details
        // Wheel Alignment and Wheel Balancing Amount
        if(((GlobalClass)getApplicationContext()).getCarList().size() > 0) {
            Intent i = new Intent(getApplicationContext(), SelectServices.class);
            i.putExtra("index", index);
            startActivity(i);
        } else {
            Toast.makeText(getApplicationContext(), "Please register a car to proceed", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getProviderImages() {

        final JSONObject object = new JSONObject();
        try {
            object.put("spId", ServiceProviderID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (object == null) return;

        WebServiceManager.getInstance(getApplicationContext()).addToRequestQueue(
                // Request a string response from the provided URL.
                new JsonObjectRequest(Request.Method.POST, ServiceProviderInfoURL, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, response.toString());
                                try {
                                    ProviderImages.add(response.getString("spImage"));
                                    serviceProviderInfoAdapter.notifyDataSetChanged();
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
                        Map<String,String> header = new HashMap<>();
                        header.put("X-ACCESS-TOKEN", AuthenticationManager.getInstance().getAccessToken());
                        Log.e(TAG,"header "+header);
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
                        Log.d(TAG, "StatusCode: "+String.valueOf(response.statusCode));
                        return super.parseNetworkResponse(response);
                    }
                }
        );
    }

}