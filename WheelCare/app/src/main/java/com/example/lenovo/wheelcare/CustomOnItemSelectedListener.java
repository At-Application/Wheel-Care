package com.example.lenovo.wheelcare;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;


/**
 * Created by Lenovo on 8/15/2017.
 */

class CustomOnItemSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
        Toast.makeText(parent.getContext(),
                "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(),
                Toast.LENGTH_LONG).show();

    }



    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
