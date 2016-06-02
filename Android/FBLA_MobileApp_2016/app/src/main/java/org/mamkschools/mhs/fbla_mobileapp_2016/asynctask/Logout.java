package org.mamkschools.mhs.fbla_mobileapp_2016.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONObject;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.Commands;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.Constants;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.SecureAPI;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.Util;

/**
 * ASynctask to logout user
 * Created by aakat on 5/9/2016.
 */
public class Logout extends AsyncTask<Void, Void, Boolean> {
    private Context context;
    private LogoutCompletionListener callback;

    public interface LogoutCompletionListener{
        void logout();
    }
    public Logout(Context context, LogoutCompletionListener callback){
        this.context = context;
        this.callback = callback;
        execute();
    }
    @Override
    protected Boolean doInBackground(Void... v) {
        SecureAPI HTTPS = SecureAPI.getInstance(context);

        try {
            JSONObject returned = HTTPS.HTTPSGET(Commands.Get.LOGOUT + Constants.AUTHCODE);
            return returned.get("status").toString().equals("success");
        } catch (Exception ex) {
            Util.log(ex.getMessage());
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean b) {
        Constants.AUTHCODE = null;
        Constants.savePrefs(context, false);
        if(callback != null){
            callback.logout();
        }
        if (!b) {
            Util.log("Logout failed");
        }
    }
}