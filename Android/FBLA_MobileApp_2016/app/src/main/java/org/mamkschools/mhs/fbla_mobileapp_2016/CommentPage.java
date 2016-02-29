package org.mamkschools.mhs.fbla_mobileapp_2016;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.app.FragmentTransaction.*;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.Constants;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.SecureAPI;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.util;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by jackphillips on 2/26/16.
 */
public class CommentPage extends AppCompatActivity  {
    ImageView myImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_page);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Bundle extras = getIntent().getExtras();
        myImage = (ImageView) findViewById(R.id.myImage);
        if (extras != null) {
            int pid = extras.getInt("pid");
            String imgTitle = extras.getString("title");
            setTitle(imgTitle);
            TextView titleText = (TextView) findViewById(R.id.myImageTitle);
            titleText.setText(imgTitle);
            new GetPicture().execute(pid);
            new GetComments().execute(pid);
        }
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
                    if(!style.equals("null")) {
                        ret.add(Comment.newInstance(user, comment.equals("null") ? "No Comment": comment, style));
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
                    ret.get(i).setShowDiv(i == ret.size() - 1);
                    transaction.add(R.id.commentList, ret.get(i), "Fragment_" + i);
                }
                transaction.commit();
            }else{
                util.log("Did not work_111");
            }
        }
    }

    private class GetPicture extends AsyncTask<Integer, Void, Boolean> {
        SecureAPI picture = SecureAPI.getInstance(getApplicationContext());
        Bitmap imageData;

        @Override
        protected Boolean doInBackground(Integer... params) {
            try{
                int pid = params[0];
                util.log("picture/" + pid + "?authcode=" + Constants.AUTHCODE);
                File picFile = new File(getFilesDir(), "picture.jpg");
                picture.HTTPSFETCHPIC("picture/" + pid + "?authcode=" + Constants.AUTHCODE, picFile);

                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                options.inMutable = true;
                if (imageData != null) {
                    options.inBitmap = imageData;
                }
                try {
                    imageData = BitmapFactory.decodeFile(picFile.getAbsolutePath(), options);
                    util.log("" + imageData.getByteCount()); //do not remove line throws exception if decoding problem
                } catch(Exception e) {
                    //Problem decoding into existing bitmap, allocate new memory
                    options.inBitmap = null;
                    imageData = BitmapFactory.decodeFile(picFile.getAbsolutePath(), options);
                }
            }catch(Exception e){
                util.log(e.getMessage());
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean v) {
            if(v){
                myImage.setImageBitmap(imageData);
            }else{
                util.log("Life will go on");
            }
        }
    }
}
