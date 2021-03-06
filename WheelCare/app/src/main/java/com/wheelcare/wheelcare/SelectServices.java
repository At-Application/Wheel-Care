package com.wheelcare.wheelcare;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Vimal on 08-09-2017.
 */

public class SelectServices extends RootActivity {

    private static final String TAG = UserHome.class.getSimpleName();

    private static final String getSlotURL = "http://" + GlobalClass.IPAddress + GlobalClass.Path + "getSlot";
    private static final String bookServiceURL = "http://" + GlobalClass.IPAddress + GlobalClass.Path + "carBookingService";

    private static final String SUCCESS = "200";

    private Typeface calibri;

    private int index;
    private int ServiceProviderID;
    private String RegistrationNumber = "";
    private String Slot = " ";
    private String ManualAmount = "300";
    private String ThreeDAmount = "300";
    private String ServiceAmount = "300";
    private String Total = "4";
    String DateViewText = "dd/mm/yyyy";

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minutes;

    private int todayYear;
    private int todayMonth;
    private int todayDay;
    private int todayHour;
    private int todayMinutes;

    boolean ThreeDChecked = false;
    boolean ManualChecked = false;

    float finalAmount = 0;

    boolean slotValidity = false;

    SelectAdapter adapter;

    static final int DATE_PICKER_ID = 1111;
    static final int TIME_PICKER_ID = 1112;

    int currentPosition = 0;
    // MARK: Initialization

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_info);
        calibri = Typeface.createFromAsset(getApplicationContext().getAssets(), "Calibri.ttf");
        setupIntentData();
        setupDate();
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
        index = (int) extras.get("index");
        currentPosition = (int) extras.get("vehicleIndex");
        ServiceProviderDetails details = ((GlobalClass)getApplicationContext()).serviceProviders.get(index);
        ServiceProviderID = details.getId();
        ManualAmount = String.valueOf(details.getManualAmount());
        ThreeDAmount = String.valueOf(details.getThreeDAmount());
        //ServiceAmount = String.valueOf(details.getServiceAmount());
        Date CurrentDate = new Date();
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        DateViewText = fmt.format(CurrentDate);
    }

    public void radioButtonClicked(View v) {
        switch(v.getId())
        {
            case R.id.WheelAlignmentCheckBox:
                ThreeDChecked = true;
                ManualChecked = false;
                break;

            case R.id.WheelBalancingCheckBox:
                ThreeDChecked = false;
                ManualChecked = true;
                break;
        }
        adapter.notifyDataSetChanged();
    }

    // MARK: Setup Tool Bar

    private void setupDate() {
        final Calendar c = Calendar.getInstance();
        todayYear = year = c.get(Calendar.YEAR);
        todayMonth = month = c.get(Calendar.MONTH);
        todayDay = day = c.get(Calendar.DAY_OF_MONTH);
        todayHour = hour = c.get(Calendar.HOUR_OF_DAY);
        if(c.get(Calendar.MINUTE) > 0) {
            hour = hour + 1;
            todayMinutes = minutes = 0;
        }
        DateViewText = String.valueOf(day) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(year);
        //Slot = String.valueOf(hour) + ":" + String.valueOf(minutes);
        Slot = String.valueOf(hour) + ":00";
        checkSlotValidity();
    }

    private void setupToolbar() {
        TextView title = (TextView) findViewById(R.id.toolbar_title);
        title.setText("Select Services");
    }

    // MARK: Setup List View

    private void setupListView() {
        ListView listView = (ListView) findViewById(R.id.listView);
        adapter = new SelectAdapter();
        listView.setAdapter(adapter);
    }

    private class SelectAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 4;
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
                final CarouselView serviceProviderImage = (CarouselView) convertView.findViewById(R.id.serviceProviderImage);
                TextView selectedCar = (TextView) convertView.findViewById(R.id.serviceProviderName);
                final TextView registrationNumber = (TextView) convertView.findViewById(R.id.serviceProviderDistance);

                serviceProviderImage.setPageCount(((GlobalClass)getApplicationContext()).vehicles.size());

                selectedCar.setTypeface(calibri);
                registrationNumber.setTypeface(calibri);
                registrationNumber.setTextColor(Color.BLACK);
                registrationNumber.setTextSize(18);

                selectedCar.setText("Selected Car");
                registrationNumber.setText("");

                serviceProviderImage.setIndicatorVisibility(View.INVISIBLE);
                serviceProviderImage.setViewListener(new ViewListener() {
                    @Override
                    public View setViewForPosition(int position) {
                        View view = getLayoutInflater().inflate(R.layout.car_view, null);
                        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
                        Bitmap bmp = BitmapFactory.decodeByteArray( ((GlobalClass)getApplicationContext()).vehicles.get(position).image, 0, ((GlobalClass)getApplicationContext()).vehicles.get(position).image.length);
                        imageView.setImageBitmap(bmp);
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
                        registrationNumber.setText(((GlobalClass)getApplicationContext()).vehicles.get(position).registration_number);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });

                serviceProviderImage.setCurrentItem(currentPosition);
                registrationNumber.setText(((GlobalClass)getApplicationContext()).vehicles.get(currentPosition).registration_number);


            } else if(position == 1) {
                convertView = getLayoutInflater().inflate(R.layout.slot_details, null);
                Button calender = (Button) convertView.findViewById(R.id.button);
                TextView date = (TextView) convertView.findViewById(R.id.Date);
                TextView selectedDate = (TextView) convertView.findViewById(R.id.SelectedDate);
                TextView selectedSlot = (TextView) convertView.findViewById(R.id.SelectedSlot);
                Button timeButton = (Button) convertView.findViewById(R.id.button4);
                final TextView slot = (TextView) convertView.findViewById(R.id.slot);
                final int totalSlots = Integer.parseInt(Total);

                selectedSlot.setTypeface(calibri);
                date.setTypeface(calibri);
                selectedDate.setTypeface(calibri);
                slot.setTypeface(calibri);

                slot.setText(Slot);
                selectedDate.setText(DateViewText);

                calender.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(DATE_PICKER_ID);
                    }
                });

                timeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(TIME_PICKER_ID);
                    }
                });

            } else if(position == 2) {
                convertView = getLayoutInflater().inflate(R.layout.service_select_view, null);
                TextView selectLabel = (TextView) convertView.findViewById(R.id.SelectServices);
                TextView amountLabel = (TextView) convertView.findViewById(R.id.Amount);
                final TextView alignmentAmountLabel = (TextView) convertView.findViewById(R.id.WheelAlignmentAmount);
                TextView balancingAmountLabel = (TextView) convertView.findViewById(R.id.WheelBalancingAmount);
                final RadioButton alignmentCheckBox = (RadioButton) convertView.findViewById(R.id.WheelAlignmentCheckBox);
                final RadioButton balancingCheckBox = (RadioButton) convertView.findViewById(R.id.WheelBalancingCheckBox);

                selectLabel.setTypeface(calibri);
                amountLabel.setTypeface(calibri);
                alignmentAmountLabel.setTypeface(calibri);
                balancingAmountLabel.setTypeface(calibri);
                alignmentCheckBox.setTypeface(calibri);
                balancingCheckBox.setTypeface(calibri);

                alignmentCheckBox.setChecked(ThreeDChecked);
                balancingCheckBox.setChecked(ManualChecked);

                alignmentAmountLabel.setText("₹" + ManualAmount);
                balancingAmountLabel.setText("₹" + ThreeDAmount);

//                alignmentCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                        /*if(isChecked) {
//                            finalAmount = finalAmount + Float.parseFloat(ManualAmount);
//                        } else {
//                            finalAmount = finalAmount - Float.parseFloat(ManualAmount);
//                        }*/
//                        ThreeDChecked = isChecked;
//                        ManualChecked= !ThreeDChecked;
//                        notifyDataSetChanged();
//                    }
//                });
//
//                balancingCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                        /*if(isChecked) {
//                            finalAmount = finalAmount + Float.parseFloat(ThreeDAmount);
//                        } else {
//                            finalAmount = finalAmount - Float.parseFloat(ThreeDAmount);
//                        }*/
//                        ManualChecked = isChecked;
//                        ThreeDChecked = !ManualChecked
//                        notifyDataSetChanged();
//                    }
//                });

            } else if(position == 3){
                //convertView = null;
                convertView = getLayoutInflater().inflate(R.layout.service_total, null);
                TextView totalLabel = (TextView) convertView.findViewById(R.id.Total);
                TextView totalAmount = (TextView) convertView.findViewById(R.id.TotalAmount);

                totalLabel.setTypeface(calibri);
                totalAmount.setTypeface(calibri);

                totalLabel.setText("Total Amount");
                //finalAmount = Float.parseFloat(ServiceAmount);*/
                finalAmount = Float.parseFloat(ThreeDChecked ? ThreeDAmount : ManualAmount);

                totalAmount.setText("₹" + finalAmount);
            } else {
                convertView = null;
            }
            return convertView;
        }
    }

    // MARK: Setup Service Button

    private void setupServiceButton() {
        Button serviceButton = (Button) findViewById(R.id.proceedToService);
        serviceButton.setTypeface(calibri);
        serviceButton.setText("Confirm Booking");
    }

    // MARK: Button Pressed

    public void proceedToService(View view) {
        Log.d("Button pressed", "Proceed to pay");
        //if(ThreeDChecked || ManualChecked) {
        if(slotValidity) {
            Log.d("Button pressed", "Will Proceed to pay");
            if(((GlobalClass)getApplicationContext()).isInternetAvailable()) {
                if(((GlobalClass)getApplicationContext()).vehicles.get(currentPosition).validity != 0 && ((GlobalClass)getApplicationContext()).vehicles.get(currentPosition).validity > new Date().getTime()) {
                    Toast.makeText(getApplicationContext(), "You do not have to pay for this car", Toast.LENGTH_LONG).show();
                    bookService();
                } else {
                    //Paytm calls
                    bookService(); // Make validity = 0 in case of Paytm
                }
            }
        } else {
            Toast.makeText(this.getApplicationContext(), "This slot is not available", Toast.LENGTH_LONG).show();
            //Toast.makeText(this.getApplicationContext(), "Please select service type", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:

                // open datepicker dialog.
                // set date picker for current date
                // add pickerListener listner to date picker
                return new DatePickerDialog(this, R.style.DialogTheme , pickerListener, year, month,day);

            case TIME_PICKER_ID:

                return new TimePickerDialog(this, R.style.DialogTheme, timeSetListener, hour, minutes, true);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            year  = selectedYear;
            month = selectedMonth;
            day   = selectedDay;

            boolean proceed = true;
            if(year < todayYear || month < todayMonth || day < todayDay) {
                year = todayYear;
                month = todayMonth;
                day = todayDay;
            }

            // Show selected date
            DateViewText = String.valueOf(day) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(year);
            adapter.notifyDataSetChanged();
            checkSlotValidity();
        }
    };

    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if(minute > 0) {
                hourOfDay = hourOfDay + 1;
            }
            hour = hourOfDay;
            minutes = 0;

            if(year == todayYear && month == todayMonth && day == todayDay) {
                if(hour < todayHour) {
                    hour = todayHour;
                }
            }

            //Slot = String.valueOf(hour) + ":" + String.valueOf(minutes);
            Slot = String.valueOf(hour) + ":00";
            adapter.notifyDataSetChanged();
            checkSlotValidity();
        }
    };

    private long getSlotTime() {
        String dateString = String.valueOf(year) + " " + (month+1) + " " + day + " " + hour + ":00:00 GMT+5:30";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy MM dd HH:mm:ss z");
        try {
            Date date = sdf.parse(dateString);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // MARK: For registering CAR

    public void checkSlotValidity() {
        if(!(((GlobalClass)getApplicationContext()).isInternetAvailable())) {
            return;
        }

        Log.e("Current Time", String.valueOf(new Date().getTime()));
        Log.e("Sent Time", String.valueOf(getSlotTime()));
        if(getSlotTime() < new Date().getTime()) {
            year = todayYear;
            month = todayMonth;
            day = todayDay;
            hour = todayHour;
            minutes = todayMinutes;
            Toast.makeText(getApplicationContext(), "This slot is not available", Toast.LENGTH_LONG).show();
            slotValidity = false;
            adapter.notifyDataSetChanged();
        } else {
            JSONObject object = createJSONObject();
            if (object != null) {
                ServiceProviderCall(object);
            } else {
                Log.e(TAG, "Failed to create JSON object for fetching service provider info");
            }
        }
    }

    public JSONObject createJSONObject() {
        JSONObject object = new JSONObject();
        try {
            object.put("spId", String.valueOf(((GlobalClass)getApplicationContext()).serviceProviders.get(index).getId()));
            object.put("slotDateTime", getSlotTime());
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
                new JsonObjectRequest(Request.Method.POST, getSlotURL, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, response.toString());
                                // Actual data received here
                                try {
                                    if(!Objects.equals((String) response.get("statusDesc"), "slot is available")) {
                                        year = todayYear;
                                        month = todayMonth;
                                        day = todayDay;
                                        hour = todayHour;
                                        minutes = todayMinutes;
                                        Toast.makeText(getApplicationContext(), "This slot is not available", Toast.LENGTH_LONG).show();
                                        slotValidity = false;
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        slotValidity = true;
                                    }
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

    public void bookService() {
        JSONObject object = createServiceJSONObject();
        if(object != null) {
            bookingCall(object);
        } else {
            Log.e(TAG, "Failed to create JSON object for fetching service provider info");
        }
    }

    public JSONObject createServiceJSONObject() {
        JSONObject object = new JSONObject();
        try {
            object.put("spId", String.valueOf(((GlobalClass)getApplicationContext()).serviceProviders.get(index).getId()));
            object.put("slotDateTime", getSlotTime());
            object.put("userId", AuthenticationManager.getInstance().getUserID());
            object.put("reg_no", ((GlobalClass)getApplicationContext()).vehicles.get(currentPosition).registration_number);
            object.put("service_status", "not_verified");
            object.put("booking_service_amount", String.valueOf(finalAmount));
            object.put("validity", ((GlobalClass)getApplicationContext()).vehicles.get(currentPosition).validity); // During new booking
            String serviceType;
            if(ManualChecked) {
                serviceType = "Manual";
            } else {
                serviceType = "3D";
            }
            object.put("serviceType", serviceType);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return object;
    }

    private void backToDashboard() {
        Intent intent = new Intent(this,
                UserDashboard.class);
        intent.putExtra("reload", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void bookingCall(final JSONObject object) {
        // Add the request to the RequestQueue.
        WebServiceManager.getInstance(getApplicationContext()).addToRequestQueue(
                // Request a string response from the provided URL.
                new JsonObjectRequest(Request.Method.POST, bookServiceURL, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                               // Log.d(TAG, response.toString());
                                // Actual data received here
//                                try {
                                    // TODO: Add code for confirmation after payment
                                    backToDashboard();
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
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
