package com.wheelcare.wheelcare;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Vimal on 27-09-2017.
 */

public class UserCarList {
    private String Manufacturer;
    private String Model;
    private String Type;
    private String RegistrationNumber;
    private String Image;
    private int ServiceProivderID;
    private String serviceStatusString;
    private ServiceStatus serviceStatus;
    private ArrayList<ServiceType> serviceType;
    private String Slot;
    private String Code;

    public UserCarList(JSONObject response) {
        try {
            Manufacturer = (String) response.get("carManufacture");
            Model = (String) response.get("carName");
            Type = (String) response.get("carType");
            RegistrationNumber = (String) response.get("regNo");
            //Image = (String) response.get("");

            serviceStatusString = (String) response.get("serviceStatus");
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
                serviceType = new ArrayList<>();
                switch ((String) response.get("serviceType")) {
                    case "wheel alignment":
                        serviceType.add(ServiceType.WHEEL_ALIGNMENT);
                        break;
                    case "wheel balancing":
                        serviceType.add(ServiceType.WHEEL_BALANCING);
                        break;
                    case "balancing alignment":
                        serviceType.add(ServiceType.WHEEL_ALIGNMENT);
                        serviceType.add(ServiceType.WHEEL_BALANCING);
                        break;
                }
                Slot = (String) response.get("slot");
                Code = (String) response.get("code");
                ServiceProivderID = Integer.parseInt((String)response.get("spId"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getManufacturer() {
        return Manufacturer;
    }

    public String getModel() {
        return Model;
    }

    public String getType() {
        return Type;
    }

    public String getRegistrationNumber() {
        return RegistrationNumber;
    }

    public String getImage() {
        return Image;
    }

    public int getServiceProivderID() {
        return ServiceProivderID;
    }

    public ServiceStatus getServiceStatus() {
        if(Objects.equals(serviceStatusString, "pending")) return null;
        return serviceStatus;
    }

    public ArrayList<ServiceType> getServiceType() {
        return serviceType;
    }

    public String getCode() {
        return Code;
    }

    public String getSlot() {
        //SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy(kk:mm)");
        //return fmt.format(Slot);
        return "";
    }

    void display() {
        Log.d("DISPLAY", serviceStatusString);// + ":" + String.valueOf(serviceStatus.ordinal()));
    }
}
