package com.example.lenovo.wheelcare;

import android.app.Application;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;

/**
 * Created by Lenovo on 8/23/2017.
 */

public class MyApp extends Application {
    private Drawable spalsh_background;
    @Override
    public void onCreate() {
        super.onCreate();
        SystemClock.sleep(3000);
    }
}
