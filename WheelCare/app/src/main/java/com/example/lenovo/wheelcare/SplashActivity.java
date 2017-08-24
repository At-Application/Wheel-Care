package com.example.lenovo.wheelcare;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Lenovo on 8/23/2017.
 */

public class SplashActivity extends AppCompatActivity {
    private Drawable splash_background;
    private final int SPLASH_DISPLAY_LENGTH = 1000;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent base_intent = new Intent(SplashActivity.this,BaseActivity.class);
                SplashActivity.this.startActivity(base_intent);
                SplashActivity.this.finish();

            }
        },SPLASH_DISPLAY_LENGTH);
        /*splash_background= getResources().getDrawable(R.drawable.splash_background);
        splash_background.setBounds(0,0,100,100);
        Bitmap bitmap = Bitmap.createBitmap(100,100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        splash_background.draw(canvas);
        Intent i = new Intent(getApplicationContext(),BaseActivity.class);
        startActivity(i);
        finish();*/
    }
}
