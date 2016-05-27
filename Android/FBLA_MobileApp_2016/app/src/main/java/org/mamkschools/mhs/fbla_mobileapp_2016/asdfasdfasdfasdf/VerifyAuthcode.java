package org.mamkschools.mhs.fbla_mobileapp_2016.task;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONObject;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.Commands;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.Constants;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.SecureAPI;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.Util;

/**
 * ASyncTask to verify authcode
 * Created by aakat on 5/9/2016.
 */
public class VerifyAuthcode  extends AsyncTask <Void, Void, Boolean>{
    private InvalidAuthcodeListener callback;
    private Context context;

    public VerifyAuthcode(Context context, InvalidAuthcodeListener callback){
        this.context = context;
        this.callback = callback;
        execute();
    }
    @Override
    protected Boolean doInBackground(Void... params) {
        SecureAPI HTTPS = SecureAPI.getInstance(context);

        try {
            JSONObject returnedJSON =  HTTPS.HTTPSGET(Commands.Get.VERIFY + Constants.AUTHCODE);
            return returnedJSON.get("status").toString().equals("success");
        } catch (Exception ex) {
            Util.log(ex.getMessage());
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if(result){
            callback.onAuthcodeValid();
        } else {
            callback.onAuthcodeInvalid();
        }
    }
    public interface InvalidAuthcodeListener{
        void onAuthcodeInvalid();
        void onAuthcodeValid();
    }
}
