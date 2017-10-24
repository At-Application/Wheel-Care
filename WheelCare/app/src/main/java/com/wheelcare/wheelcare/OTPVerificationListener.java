package com.wheelcare.wheelcare;

import com.android.volley.VolleyError;

/**
 * Created by Vimal on 08-08-2017.
 */

public interface OTPVerificationListener {
    void OTPVerificationSuccessful();
    void OTPVerificationFailed(VolleyError error);
}
