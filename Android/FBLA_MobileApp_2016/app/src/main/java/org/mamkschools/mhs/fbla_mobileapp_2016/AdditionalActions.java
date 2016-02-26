package org.mamkschools.mhs.fbla_mobileapp_2016;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.mamkschools.mhs.fbla_mobileapp_2016.lib.Commands;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.Constants;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.SecureAPI;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.util;

public class AdditionalActions extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_actions);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Button logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(this);

        Button about = (Button) findViewById(R.id.aboutBtn);
        about.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.logout:
                new Logout().execute((Void) null);
                break;
            case R.id.aboutBtn:
                startActivity(new Intent(getApplicationContext(), LegalInfoActivity.class));
                break;
            default:
                Toast.makeText(getApplicationContext(), "No action", Toast.LENGTH_SHORT).show();
        }
    }

    private class Logout extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... v) {
            SecureAPI HTTPS = SecureAPI.getInstance(getApplicationContext());

            try {
                HTTPS.HTTPSGET(Commands.Get.LOGOUT + Constants.AUTHCODE);
            } catch (Exception ex) {
                util.log(ex.getMessage());
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            if(b) {
                Constants.AUTHCODE = null;
                Constants.savePrefs(getApplicationContext());
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Logout failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
