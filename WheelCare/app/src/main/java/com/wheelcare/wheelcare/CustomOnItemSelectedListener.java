package com.wheelcare.wheelcare;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;


/**
 * Created by Lenovo on 8/15/2017.
 */

class CustomOnItemSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
    TextView selected_tv;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
       /* Toast.makeText(parent.getContext(),
                "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(),
                Toast.LENGTH_LONG).show();*/
            Log.d("position", String.valueOf(pos));

    }



    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
