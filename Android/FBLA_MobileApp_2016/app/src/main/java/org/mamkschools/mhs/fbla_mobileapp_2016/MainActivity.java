package org.mamkschools.mhs.fbla_mobileapp_2016;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONObject;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.*;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static Context myContext = null;

    public static Context getContext() {
        return myContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myContext = getApplicationContext();

        Button testButton = (Button) findViewById(R.id.testHTTPS);
        testButton.setOnClickListener(this);

        Button legalButton = (Button) findViewById(R.id.legal_button);
        legalButton.setOnClickListener(this);

        Button loginActivityButton = (Button) findViewById(R.id.login_activity_btn);
        loginActivityButton.setOnClickListener(this);

        Button swipeActivityButton = (Button) findViewById(R.id.swipetest);
        swipeActivityButton.setOnClickListener(this);

        Button settingsActivityButton = (Button) findViewById(R.id.settings);
        settingsActivityButton.setOnClickListener(this);


        Constants.HTTPS = SecureAPI.getInstance(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.legal_button:
                startActivity(new Intent(getApplicationContext(), LegalInfoActivity.class));
                break;
            case R.id.testHTTPS:
                new HTTPS_TEST().execute(Commands.Get.TEST);
                break;
            case R.id.login_activity_btn:
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;
            case R.id.swipetest:
                startActivity(new Intent(getApplicationContext(), MainActivitySwipes.class));
                break;
            case R.id.settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                break;

            default:
                Toast.makeText(getApplicationContext(), "No action implemented", Toast.LENGTH_SHORT).show();
        }
    }

    private class HTTPS_TEST extends AsyncTask<String, Void, Void>{

        private ArrayList<JSONObject> ret = new ArrayList<>();

        @Override
        protected Void doInBackground(String... params) {
            for(String url : params) {
                try {
                    Constants.HTTPS = SecureAPI.getInstance();
                    ret.add(Constants.HTTPS.HTTPSGET(url));
                } catch (Exception ex) {
                    Constants.log(ex.getMessage());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            for(JSONObject jsonObject : ret) {
                String retStr = jsonObject == null ? "(null)" : jsonObject.toString();
                Constants.log(retStr);
                Toast.makeText(getApplicationContext(), retStr, Toast.LENGTH_SHORT).show();
                //TODO Any UI updates, save pic objs, db stuff.....
            }
        }
    }
}
