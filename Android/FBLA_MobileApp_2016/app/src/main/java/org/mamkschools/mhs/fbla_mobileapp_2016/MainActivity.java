package org.mamkschools.mhs.fbla_mobileapp_2016;

import android.content.Context;
import android.os.AsyncTask;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONObject;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.*;



public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static Context myContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myContext = getApplicationContext();

        Button testButton = (Button) findViewById(R.id.testHTTPS);
        testButton.setOnClickListener(this);

        Button legalButton = (Button) findViewById(R.id.legal_button);
        legalButton.setOnClickListener(this);

        Constants.HTTPS = SecureAPI.getInstance(this);

        new HTTPS_TEST().execute();
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.legal_button:
                startActivity(new Intent(this, LegalInfoActivity.class));
                break;
            case R.id.testHTTPS:
                new HTTPS_TEST().execute();
                break;
            default:
                Toast.makeText(this, "No action implemented", Toast.LENGTH_SHORT).show();
        }
    }
    public static Context getContext(){
        return myContext;
    }

    private class HTTPS_TEST extends AsyncTask <Void, Void, Void>{

        private JSONObject ret;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Constants.HTTPS = SecureAPI.getInstance();
                ret = Constants.HTTPS.HTTPSGET(Commands.TEST);
            } catch(Exception ex) {
                Log.d(Constants.DEBUG, "Exception: " + ex.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if(ret != null) {
                Log.d(Constants.DEBUG, ret.toString());
                Toast.makeText(MainActivity.getContext(), ret.toString(), Toast.LENGTH_SHORT).show();
            } else {
                Log.d(Constants.DEBUG, "(null)");
                Toast.makeText(MainActivity.getContext(), "(null)", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
