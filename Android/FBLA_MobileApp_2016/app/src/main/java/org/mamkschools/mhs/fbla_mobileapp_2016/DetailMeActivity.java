package org.mamkschools.mhs.fbla_mobileapp_2016;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.Commands;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.CommentItem;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.CommentItemAdapter;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.Constants;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.PictureHelper;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.SecureAPI;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.Util;

import java.io.File;
import java.util.ArrayList;


/**
 * Has detailed + comment stuffs
 * Created by jackphillips on 2/26/16.
 */
public class DetailMeActivity extends AppCompatActivity  {

    ImageView myImage;
    GetPicture picDownload;
    GetComments comDownload;
    private TextView titleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_me);



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
            titleText = (TextView) findViewById(R.id.myImageTitle);
            titleText.setText(imgTitle);
            picDownload = new GetPicture();
            picDownload.execute(pid);
            comDownload = new GetComments();
            comDownload.execute(pid);
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        if(comDownload != null && !comDownload.getStatus().equals(AsyncTask.Status.FINISHED)){
            comDownload.cancel(true);
        }
        if(picDownload != null && !picDownload.getStatus().equals(AsyncTask.Status.FINISHED)){
            picDownload.cancel(true);
        }
    }


    private class GetComments extends AsyncTask<Integer, Boolean, Boolean> {

        private ArrayList<CommentItem> ret = new ArrayList<>();
        SecureAPI picture = SecureAPI.getInstance(getApplicationContext());


        @Override
        protected Boolean doInBackground(Integer... params) {

            try {
                JSONObject response = picture.HTTPSGET("picture/" + params[0] + "/comment?authcode=" + Constants.AUTHCODE);
                Util.log("picture/" + params[0] + "/comment?authcode=" + Constants.AUTHCODE);

                JSONArray array = response.getJSONArray("data");

                for(int i = 0; i < array.length(); i++ ){
                    String style = array.getJSONObject(i).getString("style");
                    String user = array.getJSONObject(i).getString("username");
                    String comment = array.getJSONObject(i).getString("comment");
                    if(!style.equals("null")) {
                        ret.add(new CommentItem(
                                comment.equals("null") ? "No comment" : comment, user, style));
                    }

                }
            }catch (Exception e){
                if(Util.DEBUG_MODE){
                    Util.log("mypics error " + e.getMessage());
                }
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean v) {
            if(v){
                Util.log("Finished getting my pics");
                RecyclerView commentList = (RecyclerView) findViewById(R.id.commentList);

                LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
                assert commentList != null;
                commentList.setLayoutManager(layoutManager);

                commentList.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));

                CommentItemAdapter adapter = new CommentItemAdapter(ret,getApplicationContext());
                commentList.setAdapter(adapter);
            } else {
                Util.log("Did not work_111");
            }
        }
    }

    private class GetPicture extends AsyncTask<Integer, Void, Boolean> {
        SecureAPI picture = SecureAPI.getInstance(getApplicationContext());
        Bitmap imageData;
        File picFile;

        @Override
        protected Boolean doInBackground(Integer... params) {
            try{
                int pid = params[0];
                String picFileName = Commands.Get.RAW_PIC + pid + Commands.AUTHCODE_BASE + Constants.AUTHCODE;
                Util.log(picFileName);
                picFile = new File(getFilesDir(), "picture.jpg");
                picture.HTTPSFETCHPIC(picFileName, picFile);
            }catch(Exception e){
                Util.log(e.getMessage());
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean v) {
            if(v){
                Constants.imageBitmap = PictureHelper.getPictureBitmap(picFile);
                myImage.setImageBitmap(Constants.imageBitmap);
                myImage.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        new PictureDialog(DetailMeActivity.this,
                                Constants.imageBitmap, titleText.getText().toString()).show();
                    }
                });
            }else{
                Util.log("Life will go on");
            }
        }
    }
}
