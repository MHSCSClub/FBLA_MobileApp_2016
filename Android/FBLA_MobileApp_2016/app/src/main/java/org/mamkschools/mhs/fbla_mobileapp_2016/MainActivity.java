package org.mamkschools.mhs.fbla_mobileapp_2016;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){

            default:
                Toast.makeText(this, "No action implemented", Toast.LENGTH_SHORT).show();
        }
    }
}
