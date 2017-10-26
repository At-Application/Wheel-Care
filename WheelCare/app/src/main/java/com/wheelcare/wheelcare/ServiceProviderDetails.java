package com.wheelcare.wheelcare;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sri surya on 07-09-2017.
 */

public class ServiceProviderDetails {
    private int id;
    private String CompanyName;
    private double Latitude;
    private double Longitude;
    private String Address;
    private String Website;
    private String ContactNumber;
    private float WheelBalancingAmount;
    private float WheelAlignmentAmount;
    private ArrayList<String> LocationImage;

    public ServiceProviderDetails(JSONObject response) {
        try {
            id = Integer.parseInt((String)response.get("spId"));
            CompanyName = (String) response.get("company");
            Latitude = Double.parseDouble((String)response.get("lat"));
            Longitude = Double.parseDouble((String)response.get("lng"));
            Address = (String) response.get("address");
            Website = response.isNull("website") ? "" : (String) response.get("website");
            ContactNumber = (String) response.get("contactNo");
            WheelBalancingAmount = response.isNull("wheel_balancing_service") ? 0 : Float.parseFloat((String)response.get("wheel_balancing_service"));
            WheelAlignmentAmount = response.isNull("wheel_alignment_service") ? 0 : Float.parseFloat((String)response.get("wheel_alignment_service"));
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public LatLng getLocation() {
        return new LatLng(Latitude, Longitude);
    }

    public String getAddress() {
        return Address;
    }

    public String getWebsite() {
        return Website;
    }

    public String getContactNumber() {
        return ContactNumber;
    }

    public float getWheelBalancingAmount() {
        return WheelBalancingAmount;
    }

    public float getWheelAlignmentAmount() {
        return WheelAlignmentAmount;
    }

    public ArrayList<String> getLocationImage() {
        return LocationImage;
    }

    public int getId() {
        return id;
    }
}