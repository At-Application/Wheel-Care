package com.example.lenovo.wheelcare;

import com.android.volley.VolleyError;

/**
 * Created by Vimal on 08-08-2017.
 */

public interface RegistrationListener {
    void registrationSuccessful();
    void registrationFailed(VolleyError error);
}
