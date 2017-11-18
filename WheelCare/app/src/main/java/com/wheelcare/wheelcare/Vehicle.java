package com.wheelcare.wheelcare;

import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Vimal on 04-11-2017.
 */

public class Vehicle {

    public int id;
    public String manufacturer;
    public String model;
    public String type;
    public String registration_number;
    public byte[] image;
    public long validity;
    public long slot;
    public String code;

    public String companyName;
    public String address;
    public String contactNumber;
    public String website;

    public ServiceStatus serviceStatus;
    public ServiceType serviceType;

    public Vehicle() {

    }

    public Vehicle(JSONObject object) {
        try {
            id = object.getInt("model_id");
            manufacturer = object.getString("manufacturer");
            model = object.getString("car_model");
            type = object.getString("veh_type");
            registration_number = object.getString("reg_no");
            validity = object.getLong("validity");
            String string = object.getString("img");
            image = Base64.decode(string, Base64.DEFAULT);
            String serviceStatusString = (String) object.get("serviceStatus");
            Log.d("Service Status", serviceStatusString);
            switch(serviceStatusString) {
                case "not_verified": serviceStatus = ServiceStatus.NOT_VERIFIED; break;
                case "verified": serviceStatus = ServiceStatus.VERIFIED; break;
                case "started": serviceStatus = ServiceStatus.STARTED; break;
                case "inprogress": serviceStatus = ServiceStatus.IN_PROGRESS; break;
                case "finalizing": serviceStatus = ServiceStatus.FINALIZING; break;
                case "done": serviceStatus = ServiceStatus.DONE; break;
                case "cancelled": serviceStatus = ServiceStatus.DISMISS; break;
                case "pending": serviceStatus = null; break;
            }
            if(!Objects.equals(serviceStatusString, "pending")) {
                switch ((String) object.get("serviceType")) {
                    case "wheel alignment":
                        serviceType = ServiceType.THREE_D;
                        break;
                    case "wheel balancing":
                        serviceType = ServiceType.MANUAL;
                        break;
                }
                slot = Long.valueOf((String) object.get("slot"));
                code = (String) object.get("code");
                companyName = (String) object.get("companyName");
                address = (String) object.get("address");
                contactNumber = (String) object.get("contactNumber");
                website = (String) object.get("website");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getSlot() {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy(kk:mm)");
        return fmt.format(slot);
    }
}