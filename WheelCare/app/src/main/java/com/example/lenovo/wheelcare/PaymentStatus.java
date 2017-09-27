package com.example.lenovo.wheelcare;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Vimal on 18-09-2017.
 */

public class PaymentStatus extends RootActivity {

    private Typeface calibri;
    public boolean ServiceConfirmed = false;

    public String ProviderName = "Service Provider Name";
    public Bitmap CarImage;

    // MARK: Initialization

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_status_info);
        calibri = Typeface.createFromAsset(getApplicationContext().getAssets(), "Calibri.ttf");
        //setupIntentData();
        CarImage = BitmapFactory.decodeResource(getResources(), R.drawable.alto);
        setupToolbar();
        setupListView();
    }

    // MARK: Setup Tool Bar

    private void setupToolbar() {
        TextView title = (TextView) findViewById(R.id.toolbar_title);
        title.setText(ServiceConfirmed ? "Service Confirmed" : "Service Failed");
    }

    // MARK: Setup List View

    private void setupListView() {
        ListView listView = (ListView) findViewById(R.id.listView);
        PaymentAdapter paymentAdapter = new PaymentAdapter();
        listView.setAdapter(paymentAdapter);
    }

    private class PaymentAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return ServiceConfirmed ? 4 : 2;
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
                convertView = getLayoutInflater().inflate(R.layout.payment_status_image, null);
                ImageView UserCarImage = (ImageView) convertView.findViewById(R.id.serviceProviderImage);
                TextView serviceProviderName = (TextView) convertView.findViewById(R.id.serviceProviderName);

                serviceProviderName.setTypeface(calibri);

                if (CarImage != null) {
                    UserCarImage.setImageBitmap(CarImage);
                }
                if(!ServiceConfirmed) serviceProviderName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                serviceProviderName.setText(ServiceConfirmed ? ProviderName : "Slot not confirmed. Please try again.");
                return convertView;
            }
            if(ServiceConfirmed) {
                if (position == 1) {
                    convertView = getLayoutInflater().inflate(R.layout.payment_code, null);
                    TextView SlotConfirmed = (TextView) convertView.findViewById(R.id.Key);
                    TextView SelectedDate = (TextView) convertView.findViewById(R.id.Value);


                    SlotConfirmed.setTypeface(calibri);
                    SelectedDate.setTypeface(calibri);

                } else if (position == 2) {
                    convertView = getLayoutInflater().inflate(R.layout.payment_code, null);
                    TextView ConfirmedCode = (TextView) convertView.findViewById(R.id.Key);
                    TextView Code = (TextView) convertView.findViewById(R.id.Value);

                    ConfirmedCode.setTypeface(calibri);
                    Code.setTypeface(calibri);

                    ConfirmedCode.setText("Code");
                    Code.setText("LMT007");

                } else if (position == 3) {
                    convertView = getLayoutInflater().inflate(R.layout.payment_status_logo, null);
                    ImageView StatusLogo = (ImageView) convertView.findViewById(R.id.StatusLogo);
                    TextView StatusText = (TextView) convertView.findViewById(R.id.StatusText);

                    int drawableID = ServiceConfirmed ? R.drawable.payment_successful : R.drawable.payment_failed;
                    StatusLogo.setImageDrawable(getDrawable(drawableID));
                    StatusText.setText(ServiceConfirmed ? "Payment Successful" : "Payment Failed");
                } else {
                    convertView = null;
                }
            } else {
                if (position == 1) {
                    convertView = getLayoutInflater().inflate(R.layout.payment_status_logo, null);
                    ImageView StatusLogo = (ImageView) convertView.findViewById(R.id.StatusLogo);
                    TextView StatusText = (TextView) convertView.findViewById(R.id.StatusText);

                    int drawableID = ServiceConfirmed ? R.drawable.payment_successful : R.drawable.payment_failed;
                    StatusLogo.setImageDrawable(getDrawable(drawableID));
                    StatusText.setText(ServiceConfirmed ? "Payment Successful" : "Payment Failed");
                } else {
                    convertView = null;
                }
            }

            return convertView;
        }
    }
}
