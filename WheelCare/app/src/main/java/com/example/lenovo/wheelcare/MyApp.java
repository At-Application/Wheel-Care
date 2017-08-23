package com.example.lenovo.wheelcare;

import android.app.Application;
import android.os.SystemClock;

/**
 * Created by Lenovo on 8/23/2017.
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SystemClock.sleep(3000);
    }
}
