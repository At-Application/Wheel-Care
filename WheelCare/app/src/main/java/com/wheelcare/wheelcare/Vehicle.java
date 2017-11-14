package com.wheelcare.wheelcare;

import android.util.Base64;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

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
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
