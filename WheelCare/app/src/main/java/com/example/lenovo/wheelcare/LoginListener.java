package com.example.lenovo.wheelcare;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Module Name: Login Listener
 * Date: 06-08-2017.
 * Author: Vimal Gohel
 * Company: At Application
 * Description: This is the generic protocol defined for Login flow
 *
 * Note:
 * User should define these protocols in their package for getting the respective response
 *
 */

interface LoginListener {
    void loginSuccess(JSONObject response);
    void loginFailed(VolleyError error);
}
