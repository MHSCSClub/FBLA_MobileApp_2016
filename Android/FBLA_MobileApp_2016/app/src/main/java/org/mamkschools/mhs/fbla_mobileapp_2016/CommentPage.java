package org.mamkschools.mhs.fbla_mobileapp_2016;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.app.FragmentTransaction.*;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.Constants;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.SecureAPI;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.util;

import java.util.ArrayList;


/**
 * Created by jackphillips on 2/26/16.
 */
public class CommentPage extends FragmentActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_page);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int pid = extras.getInt("pid");
            new GetComments().execute(pid);
        }


    }

    @Override
    public void onClick(View v) {

    }
    private class GetComments extends AsyncTask<Integer, Boolean, Boolean> {

        private ArrayList<Comment> ret = new ArrayList<>();
        SecureAPI picture = SecureAPI.getInstance(getApplicationContext());


        @Override
        protected Boolean doInBackground(Integer... params) {

            try {
                JSONObject response = picture.HTTPSGET("picture/" + params[0] + "/comment?authcode=" + Constants.AUTHCODE);
                util.log("picture/" + params[0] + "/comment?authcode=" + Constants.AUTHCODE);

                JSONArray array = response.getJSONArray("data");

                for(int i = 0; i < array.length(); i++ ){
                    String style = array.getJSONObject(i).getString("style");
                    String user = array.getJSONObject(i).getString("username");
                    String comment = array.getJSONObject(i).getString("comment");
                    if(style != "null") {
                        ret.add(Comment.newInstance(user, comment == "null"? "No Comment": comment, style));
                    }

                }
            }catch (Exception e){
                if(Constants.DEBUG_MODE){
                    util.log("mypics error " + e.getMessage());
                }
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean v) {
            if(v){
                util.log("Finished getting my pics");
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                for(int i = 0; i < ret.size(); i++){
                    transaction.add(R.id.view_pager, ret.get(i), "Fragment_" + i);
                }
                transaction.commit();
            }else{
                util.log("Did not work_111");
            }
        }
    }
}
