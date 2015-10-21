package org.mamkschools.mhs.fbla_mobileapp_2016;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.*;



public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static Context myContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myContext = getApplicationContext();

        Constants.HTTPS = SecureAPI.getInstance();
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){

            default:
                Toast.makeText(this, "No action implemented", Toast.LENGTH_SHORT).show();
        }
    }
    public static Context getContext(){
        return myContext;
    }
    private class HTTPS_TEST extends AsyncTask <Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }
    }
}
