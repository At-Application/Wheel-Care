package com.wheelcare.wheelcare;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

/**
 * Created by sri surya on 07-09-2017.
 */

public class UserHistoryDetails {
    public int modelId;
    public int spiId;
    public String vehicleRegistrationNumber;
    public Date date_slot;
    public ServiceType serviceRequired;
    public String code;
    public ServiceStatus serviceStatus;
    public String issue;
    public String comment;
    public float amount;


    public UserHistoryDetails(JSONObject obj) {
        try {
            amount = Float.parseFloat(obj.getString("booking_service_amount"));
            spiId = Integer.parseInt(obj.getString("spId"));
            modelId = Integer.parseInt(obj.getString("model_id"));

            vehicleRegistrationNumber = (String) obj.get("reg_no");
            long date = Long.valueOf(obj.getString("slot_time"));
            date_slot = new Date(date);
            code = (String) obj.get("code");

            String serviceStatusString = (String) obj.get("service_status");
            switch (serviceStatusString) {
                case "not_verified":
                    serviceStatus = ServiceStatus.NOT_VERIFIED;
                    break;
                case "verified":
                    serviceStatus = ServiceStatus.VERIFIED;
                    break;
                case "started":
                    serviceStatus = ServiceStatus.STARTED;
                    break;
                case "inprogress":
                    serviceStatus = ServiceStatus.IN_PROGRESS;
                    break;
                case "finalizing":
                    serviceStatus = ServiceStatus.FINALIZING;
                    break;
                case "done":
                    serviceStatus = ServiceStatus.DONE;
                    break;
                case "cancelled":
                    serviceStatus = ServiceStatus.DISMISS;
                    break;
            }

            String service_required = obj.getString("service_type");
            if(Objects.equals(service_required, "3D")) {
                serviceRequired = ServiceType.THREE_D;
            }
            if(Objects.equals(service_required, "Manual")) {
                serviceRequired = ServiceType.MANUAL;
            }

            issue = obj.isNull("issue") ? "" : (String) obj.get("issue");
            comment = obj.isNull("comment") ? "" : (String) obj.get("comment");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}