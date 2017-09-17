package com.example.lenovo.wheelcare;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Vimal on 08-09-2017.
 */

class SlotDetails {
    Date slotTime;

    public String getSlotTime() {
        SimpleDateFormat fmt = new SimpleDateFormat("kk");
        return fmt.format(slotTime);
    }
}

public class SelectServices extends RootActivity {

    private Typeface calibri;

    private int ServiceProviderID;
    private String RegistrationNumber = "KA04JD1234";
    private String Slot = "0";
    private String WheelAlignmentAmount = "300";
    private String WheelBalancingAmount = "300";
    private String Total = "4";
    private Bitmap SelectedCarImage = null;
    ArrayList<SlotDetails> slotDetails;
    String DateViewText = "27/01/2017";

    boolean alignmentChecked = false;
    boolean balancingChecked = false;

    int finalAmount = 0;
    // MARK: Initialization

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_info);
        calibri = Typeface.createFromAsset(getApplicationContext().getAssets(), "Calibri.ttf");
        //setupIntentData();
        setupToolbar();
        setupListView();
        setupServiceButton();
    }

    // MARK: Setup Intent Data

    private void setupIntentData() {
        Bundle extras = getIntent().getExtras();
        ServiceProviderID = extras.getInt("serviceProviderID");
        WheelAlignmentAmount = extras.getString("wheelAlignmentAmount");
        WheelBalancingAmount = extras.getString("wheelBalancingAmount");
        slotDetails = new ArrayList<>();
        slotDetails = (ArrayList<SlotDetails>) getIntent().getSerializableExtra("slotDetails");
        Date CurrentDate = (Date) getIntent().getSerializableExtra("currentDate");
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        DateViewText = fmt.format(CurrentDate);
    }

    // MARK: Setup Tool Bar

    private void setupToolbar() {
        TextView title = (TextView) findViewById(R.id.toolbar_title);
        title.setText("Select Services");
    }

    // MARK: Setup List View

    private void setupListView() {
        ListView listView = (ListView) findViewById(R.id.listView);
        SelectAdapter adapter = new SelectAdapter();
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
                ImageView serviceProviderImage = (ImageView) convertView.findViewById(R.id.serviceProviderImage);
                TextView selectedCar = (TextView) convertView.findViewById(R.id.serviceProviderName);
                TextView registrationNumber = (TextView) convertView.findViewById(R.id.serviceProviderDistance);

                selectedCar.setTypeface(calibri);
                registrationNumber.setTypeface(calibri);
                registrationNumber.setTextColor(Color.BLACK);
                registrationNumber.setTextSize(18);

                if (SelectedCarImage != null) {
                    serviceProviderImage.setImageBitmap(SelectedCarImage);
                }
                selectedCar.setText("Selected Car");
                registrationNumber.setText(RegistrationNumber);

            } else if(position == 1) {
                convertView = getLayoutInflater().inflate(R.layout.slot_details, null);
                TextView date = (TextView) convertView.findViewById(R.id.Date);
                TextView selectedDate = (TextView) convertView.findViewById(R.id.SelectedDate);
                TextView selectedSlot = (TextView) convertView.findViewById(R.id.SelectedSlot);
                final TextView slot = (TextView) convertView.findViewById(R.id.slot);
                LinearLayout slotLayout = (LinearLayout) convertView.findViewById(R.id.slotView);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) slotLayout.getLayoutParams();
                int totalSlots = Integer.parseInt(Total);

                slotLayout.setWeightSum(totalSlots);

                date.setTypeface(calibri);
                selectedDate.setTypeface(calibri);
                selectedDate.setTypeface(calibri);
                slot.setTypeface(calibri);

                selectedDate.setText(DateViewText);

                float slotWidth = params.width/totalSlots;
                float slotHeight = params.height;

                for(int i = 0; i<totalSlots; i++) {
                    Button b = new Button(getApplicationContext());
                    LinearLayout.LayoutParams bParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
                    b.setLayoutParams(bParams);
                    b.setText(String.valueOf(i));
                    b.setTextSize(12);
                    b.setBackground(getDrawable(R.drawable.border_green));
                    b.setTypeface(calibri);
                    b.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    b.setGravity(Gravity.CENTER_VERTICAL);
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Button button = (Button)v;
                            slot.setText(button.getText());
                            Slot = (String) slot.getText();
                        }
                    });
                    slotLayout.addView(b);
                }
            } else if(position == 2) {
                convertView = getLayoutInflater().inflate(R.layout.service_select_view, null);
                TextView selectLabel = (TextView) convertView.findViewById(R.id.SelectServices);
                TextView amountLabel = (TextView) convertView.findViewById(R.id.Amount);
                final TextView alignmentAmountLabel = (TextView) convertView.findViewById(R.id.WheelAlignmentAmount);
                TextView balancingAmountLabel = (TextView) convertView.findViewById(R.id.WheelBalancingAmount);
                final CheckBox alignmentCheckBox = (CheckBox) convertView.findViewById(R.id.WheelAlignmentCheckBox);
                CheckBox balancingCheckBox = (CheckBox) convertView.findViewById(R.id.WheelBalancingCheckBox);

                selectLabel.setTypeface(calibri);
                amountLabel.setTypeface(calibri);
                alignmentAmountLabel.setTypeface(calibri);
                balancingAmountLabel.setTypeface(calibri);
                alignmentCheckBox.setTypeface(calibri);
                balancingCheckBox.setTypeface(calibri);

                alignmentCheckBox.setChecked(alignmentChecked);
                balancingCheckBox.setChecked(balancingChecked);

                alignmentCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked) {
                            finalAmount = finalAmount + 300;
                        } else {
                            finalAmount = finalAmount - 300;
                        }
                        alignmentChecked = isChecked;
                        notifyDataSetChanged();
                    }
                });

                balancingCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked) {
                            finalAmount = finalAmount + 300;
                        } else {
                            finalAmount = finalAmount - 300;
                        }
                        balancingChecked = isChecked;
                        notifyDataSetChanged();
                    }
                });

            } else if(position == 3){
                convertView = getLayoutInflater().inflate(R.layout.service_total, null);
                TextView totalLabel = (TextView) convertView.findViewById(R.id.Total);
                TextView totalAmount = (TextView) convertView.findViewById(R.id.TotalAmount);

                totalLabel.setTypeface(calibri);
                totalAmount.setTypeface(calibri);

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
        serviceButton.setText("PROCEED TO PAY");
    }

    // MARK: Button Pressed

    public void proceedToService(View view) {

    }

}
