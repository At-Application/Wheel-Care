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
    //private float ServiceAmount;
    private float ThreeDAmount;
    private float ManualAmount;
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
            //ServiceAmount = response.isNull("balancing_alignment_service") ? 0 : Float.parseFloat((String)response.get("balancing_alignment_service"));
            ThreeDAmount = response.isNull("3D") ? 0 : Float.parseFloat((String)response.get("3D"));
            ManualAmount = response.isNull("Manual") ? 0 : Float.parseFloat((String)response.get("Manual"));
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

    public float getThreeDAmount() {
        return ThreeDAmount;
    }

    public float getManualAmount() {
        return ManualAmount;
    }

    public ArrayList<String> getLocationImage() {
        return LocationImage;
    }

    public int getId() {
        return id;
    }

    //public float getServiceAmount() {
      //  return ServiceAmount;
    //}
}