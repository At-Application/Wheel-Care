package com.wheelcare.wheelcare;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Date: 06-08-2017.
 * Author: Vimal Gohel
 * Company: At Application
 * Description: This is singleton reference to call all the web services
 */

public class WebServiceManager {

    private static WebServiceManager ourInstance;

    private RequestQueue queue;

    private static Context webContext;

    private WebServiceManager(Context context) {
        webContext = context;
        queue = getRequestQueue();
    }

    public static synchronized WebServiceManager getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new WebServiceManager(context);
        }
        return ourInstance;
    }

    public RequestQueue getRequestQueue() {
        if (queue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            queue = Volley.newRequestQueue(webContext.getApplicationContext());
        }
        return queue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
