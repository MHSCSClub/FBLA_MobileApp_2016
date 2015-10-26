package org.mamkschools.mhs.fbla_mobileapp_2016;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONObject;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.Commands;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.Constants;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.SecureAPI;


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

        Constants.HTTPS = SecureAPI.getInstance(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.legal_button:
                startActivity(new Intent(this, LegalInfoActivity.class));
                break;
            case R.id.testHTTPS:
                new HTTPS_TEST().execute();
                break;
            case R.id.login_activity_btn:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            default:
                Toast.makeText(this, "No action implemented", Toast.LENGTH_SHORT).show();
        }
    }

    private class HTTPS_TEST extends AsyncTask<Void, Void, Void> {

        private JSONObject ret;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Constants.HTTPS = SecureAPI.getInstance();
                ret = Constants.HTTPS.HTTPSGET(Commands.TEST);
            } catch (Exception ex) {
                Log.d(Constants.DEBUG, "Exception: " + ex.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            String retStr = ret == null ? "(null)" : ret.toString();
            Log.d(Constants.DEBUG, retStr);
            Toast.makeText(MainActivity.getContext(), retStr, Toast.LENGTH_SHORT).show();
        }
    }
}
