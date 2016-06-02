package org.mamkschools.mhs.fbla_mobileapp_2016;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.Commands;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.CommentItem;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.CommentItemAdapter;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.Constants;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.SecureAPI;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.Util;
import org.mamkschools.mhs.fbla_mobileapp_2016.asynctask.Logout;
import org.mamkschools.mhs.fbla_mobileapp_2016.asynctask.VerifyAuthcode;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;


/**
 * Has detailed + comment stuffs
 * Created by jackphillips on 2/26/16.
 */
public class DetailMeActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener, VerifyAuthcode.InvalidAuthcodeListener {

    private ImageView myImage;
    private GetPicture picDownload;
    private GetComments comDownload;
    private GetMyPictureInfo getInfo;
    private SwipeRefreshLayout refreshLayout;
    private int pid;
    private int refreshing;
    private Bitmap image;
    private ProgressBar progressBar;

    private TextView avgStar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_me);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.detail_refresh);
        assert refreshLayout != null;
        refreshLayout.setOnRefreshListener(this);
        progressBar = (ProgressBar) findViewById(R.id.detail_progress);
        assert progressBar != null;

        new VerifyAuthcode(this, this);

        showProgress(true);
        TextView percentLabel = (TextView) findViewById(R.id.percent_label);
        TextView viewsLabel = (TextView) findViewById(R.id.views_label);
        avgStar = (TextView) findViewById(R.id.rate_label);

        Bundle extras = getIntent().getExtras();
        myImage = (ImageView) findViewById(R.id.myImage);
        if (extras != null) {
            pid = extras.getInt("pid");
            String imgTitle = extras.getString("title");
            int views = extras.getInt("views");
            double up = extras.getInt("up");
            Util.log("" + up);
            if(views > 0) {
                double percent = (up / views) * 100;
                percentLabel.setText("" +  ((int) percent) + "%");
                if(percent < 50){
                    percentLabel.setTextColor(Color.parseColor("#ED332D"));
                }else{
                    percentLabel.setTextColor(Color.parseColor("#2E7D32"));
                }
            }else{
                percentLabel.setText("N/A");
            }
            viewsLabel.setText("" + views);
            setTitle(imgTitle);
            onRefresh();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_me_activity, menu);
        return true;
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
        if(getInfo != null && !getInfo.getStatus().equals(AsyncTask.Status.FINISHED)){
            getInfo.cancel(true);
        }
    }

    @Override
    public void onRefresh() {
        new VerifyAuthcode(this, this);
        refreshing = 3;
        if(refreshLayout.isRefreshing()) {
            getInfo = new GetMyPictureInfo();
            getInfo.execute();
        }
        picDownload = new GetPicture();
        picDownload.execute(pid);
        comDownload = new GetComments();
        comDownload.execute(pid);
    }


    private class GetMyPictureInfo extends AsyncTask<Void, Boolean, Boolean> {
        SecureAPI picture = SecureAPI.getInstance(getApplicationContext() );
        int views, likes;


        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                JSONObject response = picture.HTTPSGET("picture/fetch/me?authcode=" + Constants.AUTHCODE);
                Util.log("picture/fetch/me?authcode=" + Constants.AUTHCODE);

                JSONArray array = response.getJSONArray("data");

                for(int i = 0; i < array.length(); i++ ) {
                    int dlPid = array.getJSONObject(i).getInt("pid");


                    if (pid == dlPid) {
                        views = array.getJSONObject(i).getInt("views");
                        likes = array.getJSONObject(i).getInt("likes");
                        return true;
                    }
                }
            }catch (Exception e){
                if(Constants.DEBUG_MODE){
                    Util.log("mypics error " + e.getMessage());
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if(success){
                TextView percentLabel = (TextView) findViewById(R.id.percent_label);
                TextView viewsLabel = (TextView) findViewById(R.id.views_label);
                if(views > 0) {
                    double percent = (likes / views) * 100;
                    percentLabel.setText("" +  ((int) percent) + "%");
                    if(percent < 50){
                        percentLabel.setTextColor(Color.parseColor("#ED332D"));
                    }else{
                        percentLabel.setTextColor(Color.parseColor("#2E7D32"));
                    }
                }else{
                    percentLabel.setText("N/A");
                }
                viewsLabel.setText("" + views);
            }
            stopRefresh();
        }
    }
    @Override
    public void onAuthcodeInvalid() {
        new Logout(this, null);
    }

    @Override
    public void onAuthcodeValid() {
        //We don't need to do anything...
    }


    private class GetComments extends AsyncTask<Integer, Boolean, Boolean> {

        private ArrayList<CommentItem> ret = new ArrayList<>();
        SecureAPI picture = SecureAPI.getInstance(getApplicationContext());
        private int totalStyle;
        private int totalComments = 0;

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
                        int styleInt = Integer.parseInt(style);
                        if(styleInt < 1 || styleInt > 5){
                            break;
                        }
                        totalStyle += styleInt;
                        Util.log(style);
                        totalComments++;
                    }


                }
            }catch (Exception e){
                if(Constants.DEBUG_MODE){
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
                if(ret.size() != 0) {
                    //noinspection ConstantConditions
                    findViewById(R.id.nocomment_text).setVisibility(View.GONE);

                    RecyclerView commentList = (RecyclerView) findViewById(R.id.commentList);
                    assert commentList != null;
                    commentList.setVisibility(View.VISIBLE);

                    LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                    commentList.setLayoutManager(layoutManager);

                    //commentList.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));

                    CommentItemAdapter adapter = new CommentItemAdapter(ret, getApplicationContext());
                    commentList.setAdapter(adapter);
                    double avg = (float) totalStyle/totalComments;
                    if(avg == 0){
                        avgStar.setText("N/A");
                    } else {
                        DecimalFormat df = new DecimalFormat("#.0");
                        avgStar.setText(df.format(avg));
                    }
                } else {
                    //noinspection ConstantConditions
                    findViewById(R.id.nocomment_text).setVisibility(View.VISIBLE);
                    //noinspection ConstantConditions
                    findViewById(R.id.commentList).setVisibility(View.GONE);
                    avgStar.setText("N/A");
                }
            } else {
                Util.log("Did not work_111");
            }
            stopRefresh();
        }
    }

    private class GetPicture extends AsyncTask<Integer, Void, Boolean> {
        SecureAPI picture = SecureAPI.getInstance(getApplicationContext());
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
            stopRefresh();
            if(v){
                image = Util.getPictureBitmap(picFile);
                myImage.setImageBitmap(image);
            }else{
                Util.log("Life will go on");
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_show_picture:
                myImage.setVisibility(myImage.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                findViewById(R.id.detail_content).requestLayout();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void stopRefresh(){
        refreshing--;
        if(refreshing <= 0 && refreshLayout.isRefreshing()){
            refreshLayout.setRefreshing(false);
            showProgress(false);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(image != null){
            image.recycle();
        }
    }

    private void showProgress(boolean show){
        refreshLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
