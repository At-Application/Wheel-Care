package com.wheelcare.wheelcare;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import com.wheelcare.wheelcare.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.opengles.GL;

import static android.graphics.Typeface.BOLD;

/**
 * Created by Vimal on 08-09-2017.
 */

public class ServiceInfo extends RootActivity {

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
    private int vehicleIndex = -1;
    private ArrayList<ServiceType> serviceTypes;
    private ServiceStatus serviceStatus = ServiceStatus.FINALIZING;
    String date, boldCode;
    int index = 0;
    Context context;

    // MARK: Initialization

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_info);
        calibri = Typeface.createFromAsset(getApplicationContext().getAssets(), "Calibri.ttf");
        context = getApplicationContext();
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
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            index = (int) extra.get("UserCarListIndex");
            vehicleIndex = (int) extra.get("vehicleIndex");
        }
        UserCarList obj = ((GlobalClass)context).userCarLists.get(index);
        if (obj.getServiceStatus() != null) {
            RegistrationNumber = obj.getRegistrationNumber();
            serviceStatus = obj.getServiceStatus();
            date = obj.getSlot();
            boldCode = obj.getCode();
            serviceTypes = obj.getServiceType();
            ServiceProviderID = obj.getServiceProivderID();
            //ProviderImage = obj.getImage();
        }

        for(ServiceProviderDetails sobj: ((GlobalClass)context).serviceProviders) {
            if(sobj.getId() == ServiceProviderID) {
                Name = sobj.getCompanyName();
                Address = sobj.getAddress();
                PhoneNumber = sobj.getContactNumber();
                Website = sobj.getWebsite();
                //ProviderImage = BitmapFactory.decodeByteArray(getIntent().getByteArrayExtra("image"), 0, getIntent().getByteArrayExtra("image").length);
            }
        }
    }

    // MARK: Setup Tool Bar

    private void setupToolbar() {
        TextView title = (TextView) findViewById(R.id.toolbar_title);
        title.setText("Status");
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
                convertView = getLayoutInflater().inflate(R.layout.service_status_info, null);
                ImageView CarImage = (ImageView) convertView.findViewById(R.id.CarImage);
                final TextView registrationNumber = (TextView) convertView.findViewById(R.id.RegistrationNumber);
                final TextView wheelAlignment = (TextView) convertView.findViewById(R.id.WheelAlignmentLabel);
                final TextView wheelBalancing = (TextView) convertView.findViewById(R.id.WheelBalancingLabel);
                final TextView code = (TextView) convertView.findViewById(R.id.Code);
                final TextView codeVerified = (TextView) convertView.findViewById(R.id.CodeVerified);
                final TextView dateSlot = (TextView) convertView.findViewById(R.id.dateSlot);
                final TextView started = (TextView) convertView.findViewById(R.id.Started);
                final TextView inProgress = (TextView) convertView.findViewById(R.id.InProgress);
                final TextView finalizing = (TextView) convertView.findViewById(R.id.Finalizing);
                final TextView done = (TextView) convertView.findViewById(R.id.Done);

                registrationNumber.setTypeface(calibri);
                codeVerified.setTypeface(calibri);
                wheelAlignment.setTypeface(calibri);
                wheelBalancing.setTypeface(calibri);
                code.setTypeface(calibri, BOLD);
                dateSlot.setTypeface(calibri);

                started.setTextSize(12);
                inProgress.setTextSize(12);
                finalizing.setTextSize(12);
                done.setTextSize(12);
                code.setTextSize(25);

                Log.d("vehicleIndex", String.valueOf(vehicleIndex));

                Bitmap bmp = BitmapFactory.decodeByteArray(((GlobalClass)context).vehicles.get(vehicleIndex).image, 0, ((GlobalClass)context).vehicles.get(vehicleIndex).image.length);
                CarImage.setImageBitmap(bmp);

                registrationNumber.setText(RegistrationNumber);

                dateSlot.setText(date);

                code.setText("Code: " + boldCode);

                if(serviceTypes.contains(ServiceType.WHEEL_ALIGNMENT)) {
                    wheelAlignment.setVisibility(View.VISIBLE);
                } else {
                    wheelAlignment.setVisibility(View.INVISIBLE);
                }

                if(serviceTypes.contains(ServiceType.WHEEL_BALANCING)) {
                    wheelBalancing.setVisibility(View.VISIBLE);
                } else {
                    wheelBalancing.setVisibility(View.INVISIBLE);
                }

                switch (serviceStatus) {
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

                    case VERIFIED:
                        codeVerified.setText("Code Verified");
                        break;

                    default: codeVerified.setText("Code Not Verified");
                }

            } else if(position == 1) {
                convertView = getLayoutInflater().inflate(R.layout.payment_code, null);
                TextView serviceProviderName = (TextView) convertView.findViewById(R.id.Key);
                TextView makeEmpty = (TextView) convertView.findViewById(R.id.Value);
                makeEmpty.setVisibility(View.INVISIBLE);

                serviceProviderName.setTypeface(calibri);
                serviceProviderName.setText(Name);

            } else {
                convertView = getLayoutInflater().inflate(R.layout.service_provider_details, null);
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
                        if(lines == 2) {
                            detailTypeText.setHeight(60);
                        } else if(lines == 3) {
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
                        convertView = new View(getApplicationContext());
                        convertView.setMinimumHeight(5);
                        convertView.setBackgroundResource(R.color.white);
                }
            }
            return convertView;
        }
    }

    // MARK: Setup Service Button

    private void setupServiceButton() {
        Button serviceButton = (Button) findViewById(R.id.proceedToService);
        serviceButton.setText("CANCEL");
        serviceButton.setTypeface(calibri);
        if(serviceStatus != ServiceStatus.NOT_VERIFIED) {
            serviceButton.setVisibility(View.INVISIBLE);
        }
    }

    // MARK: Button Pressed

    private void closeActivity() {
        this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
        this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
    }

    public void proceedToService(View view) {
        if(!(((GlobalClass)getApplicationContext()).isInternetAvailable())) {
            return;
        }
        final JSONObject object = new JSONObject();
        try {
            object.put("userId", AuthenticationManager.getInstance().getUserID());
            object.put("reg_no", RegistrationNumber);
            object.put("service_status", "not_verified");
            if(((GlobalClass)context).vehicles.get(vehicleIndex).validity == 0 || ((GlobalClass)context).vehicles.get(vehicleIndex).validity < new Date().getTime()) {
                ((GlobalClass) context).vehicles.get(vehicleIndex).validity = (new Date().getTime() + (86400 * 1000 * 3));
                object.put("validity", ((GlobalClass) context).vehicles.get(vehicleIndex).validity);
            } else {
                object.put("validity", ((GlobalClass) context).vehicles.get(vehicleIndex).validity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(object == null) return;

        WebServiceManager.getInstance(getApplicationContext()).addToRequestQueue(
                // Request a string response from the provided URL.
                new JsonObjectRequest(Request.Method.POST, URL, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                ((GlobalClass)context).userCarLists.remove(index);
                                ((GlobalClass)context).saveCarList(((GlobalClass)context).vehicles);
                                closeActivity();
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

}