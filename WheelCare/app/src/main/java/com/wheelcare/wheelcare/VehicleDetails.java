package com.wheelcare.wheelcare;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sri surya on 07-09-2017.
 */

public class VehicleDetails {
    public Bitmap vehicleImage;
    public String vehicleRegistrationNumber;
    public String customername;
    public Date date_slot;
    public ArrayList<ServiceType> serviceRequired;
    public String code;
    public ServiceStatus serviceStatus;
    public String issue;
    public String comment;
    public String userID;
}