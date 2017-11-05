package com.wheelcare.wheelcare;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by Vimal on 06-11-2017.
 */

public interface ForgotPasswordListener {
    void ResponseSuccess(JSONObject object);
    void ResponseFailure(VolleyError error);
}
