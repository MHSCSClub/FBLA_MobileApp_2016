// Use this for showing/hiding  info layout: showInfoLayout(boolean b);



package org.mamkschools.mhs.fbla_mobileapp_2016;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.Commands;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.Constants;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.Picture;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.PictureHelper;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.SecureAPI;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.Util;
import org.mamkschools.mhs.fbla_mobileapp_2016.task.Logout;
import org.mamkschools.mhs.fbla_mobileapp_2016.task.VerifyAuthcode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import im.delight.android.location.SimpleLocation;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

/**
 * Fragment for evaluating pictures. This is on the main screen when the app is launched.
 * Created by jackphillips on 2/16/16.
 */
public class FragmentEvaluate extends Fragment implements View.OnClickListener, VerifyAuthcode.InvalidAuthcodeListener {
    private int picNumber;
    private File location;
    private SimpleLocation simpleLocation;
    private boolean runOnce = true;

    private View rootView;

    private ImageView image;
    private TextView descriptionLabel;
    private TextView titleLabel;
    private TextView timeLabel;
    private TextView distLabel;

    private int currentRating;

    private ArrayList<Picture> pics = new ArrayList<Picture>();

    private GetPicture picDl;

    private RelativeLayout [] bottomBars = new RelativeLayout[3];

    private ScrollView evalContent;
    private ProgressBar progressBar;

    public static FragmentEvaluate newInstance(int picNumber, File location, SimpleLocation simpleLocation) {
        FragmentEvaluate fragment = new FragmentEvaluate();
        fragment.picNumber = picNumber;
        fragment.location = location;
        fragment.simpleLocation = simpleLocation;
        return fragment;
    }

    public FragmentEvaluate() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_view_evaluate, container, false);
        titleLabel = (TextView) rootView.findViewById(R.id.title_label);
        titleLabel.setText(R.string.no_pics);
        timeLabel = (TextView) rootView.findViewById(R.id.time_label);
        distLabel = (TextView) rootView.findViewById(R.id.dist_label);


        image = (ImageView) rootView.findViewById(R.id.imageView);
        image.setOnClickListener(this);
        descriptionLabel = (TextView) rootView.findViewById(R.id.description_label);

        Button up_button = (Button) rootView.findViewById(R.id.up_button);
        up_button.setOnClickListener(this);

        Button down_button = (Button) rootView.findViewById(R.id.down_button);
        down_button.setOnClickListener(this);

        Button back_button = (Button) rootView.findViewById(R.id.back_button);
        back_button.setOnClickListener(this);

        Button comment_button = (Button) rootView.findViewById(R.id.comment_button);
        comment_button.setOnClickListener(this);

        Button cancel_button = (Button) rootView.findViewById(R.id.cancel_button);
        cancel_button.setOnClickListener(this);

        Button refresh_button = (Button) rootView.findViewById(R.id.refresh_button);
        refresh_button.setOnClickListener(this);

        Button dress_code = (Button) rootView.findViewById(R.id.dress_button);
        dress_code.setOnClickListener(this);

        progressBar = (ProgressBar) rootView.findViewById(R.id.eval_progress);
        evalContent = (ScrollView) rootView.findViewById(R.id.eval_content);

        bottomBars[0] = (RelativeLayout) rootView.findViewById(R.id.primary_layout);
        bottomBars[1] = (RelativeLayout) rootView.findViewById(R.id.comment_layout);
        bottomBars[2] = (RelativeLayout) rootView.findViewById(R.id.refresh_layout);
        runFetch(picNumber);

        if(pics.size() > 0 && picNumber < pics.size()) {
            Util.log("From: " + picNumber);
            int itemId = pics.get(picNumber).entryid;
            String title = pics.get(picNumber).title;
            String user = pics.get(picNumber).username;
            titleLabel.setText(title.length() > 20 ? title.substring(0, 20) : title);
            descriptionLabel.setText(user);
        }

        hideKeyboard();
        showTutorialRate();

        return rootView;
    }

    private void showTutorialRate() {
        MaterialShowcaseSequence seq = new MaterialShowcaseSequence(getActivity(), "TUT_RATE");

        Context context = getActivity().getApplicationContext();
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500);

        seq.setConfig(config);
        seq.addSequenceItem(getActivity().findViewById(R.id.tab_layout), context.getString(R.string.tut_eval), "GOT IT");
        seq.addSequenceItem(rootView.findViewById(R.id.up_button), context.getString(R.string.tut_up), "GOT IT");
        seq.addSequenceItem(rootView.findViewById(R.id.down_button), context.getString(R.string.tut_down), "GOT IT");
        seq.addSequenceItem(rootView.findViewById(R.id.dress_button), context.getString(R.string.tut_dress), "GOT IT");
        seq.addSequenceItem(getActivity().findViewById(R.id.more_stuff), context.getString(R.string.tut_more), "GOT IT");
        seq.start();
    }
    private void showTutorialComment() {
        MaterialShowcaseSequence seq = new MaterialShowcaseSequence(getActivity(), "TUT_COMMENT");

        Context context = getActivity().getApplicationContext();
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500);

        seq.setConfig(config);
        seq.addSequenceItem(rootView.findViewById(R.id.back_button), context.getString(R.string.tut_back), "GOT IT");
        seq.addSequenceItem(rootView.findViewById(R.id.comment_button), context.getString(R.string.tut_comment), "GOT IT");
        seq.addSequenceItem(rootView.findViewById(R.id.cancel_button), context.getString(R.string.tut_forward), "GOT IT");
        seq.start();
    }

    public int getPictureId(int picture){
        if(pics.size() > 0 && picture < pics.size()) {
            return pics.get(picture).entryid;
        }
        return -1;
    }

    public String[] getData(int picture){
        String[] data = new String[4];
        if(pics.size() > 0 && picture < pics.size()) {
            data[0] = pics.get(picture).title;
            data[1] = pics.get(picture).username;
            double hours = pics.get(picture).hours;
            double miles = pics.get(picture).dist;
            data[2] = ((int) hours) + (hours == 1 ? " hour ago" : " hours ago");
            if(hours >= 24){
                int days = (int) hours/24;
                data[2] = days + (days == 1 ? " day ago, " : " days ago");
            }
            if(miles > 100){
                miles = 50;
            }
            data[3] = ((int) miles) + (miles == 1 ? " mile away" : " miles away");
            return data;
        }
        data[0] = "No More Pictures";

        data[1] = "";
        data[2] = "";
        data[3] = "";
        return data;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (picDl != null && !picDl.getStatus().equals(AsyncTask.Status.FINISHED)) {
            picDl.cancel(true);
        }
    }

    public void runFetch(int itemId) {
        int picID = getPictureId(itemId);
        String[] data = getData(itemId);
        titleLabel.setText(data[0]);
        descriptionLabel.setText(data[1]);
        timeLabel.setText(data[2]);
        distLabel.setText(data[3]);
        switchBottomBar(0);
        if(picID > 0) {
            showProgress(true);
            picDl = new GetPicture();
            picDl.execute(picID);
        }else{
            showProgress(false);
            image.setImageResource(R.drawable.finish);
            switchBottomBar(2);
            if(runOnce) {
                image.setImageResource(R.drawable.finish);
                new GetPictureInfo().execute((Void) null);
                switchBottomBar(2);
                runOnce = false;
            }
        }
    }

    public void showInfoLayout(boolean show){
        View rootView = getView();
        assert rootView != null;
        RelativeLayout infoLayout = (RelativeLayout) rootView.findViewById(R.id.info_label);
        infoLayout.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        new VerifyAuthcode(getContext(), this);
        Map<String, String> postParams = new HashMap<>();
        int pid = getPictureId(picNumber);
        postParams.put("pid", "" + pid);

        if(pid == -1){
            Toast.makeText(getContext(), "No pictures left to rate", Toast.LENGTH_SHORT).show();
            return;
        }

        switch(v.getId()){

            case R.id.up_button:
                currentRating = 1;
                Util.log("up");
                switchBottomBar(1);
                break;
            case R.id.down_button:
                currentRating = 0;
                Util.log("down");
                switchBottomBar(1);
                break;
            case R.id.back_button:
                switchBottomBar(0);
                break;
            case R.id.comment_button:
                postParams.put("like", "" + Integer.toString(currentRating));
                getComment(postParams);
                break;
            case R.id.cancel_button:
                switchBottomBar(0);
                postParams.put("like", Integer.toString(currentRating));
                new SubmitRating().execute(postParams);
                break;
            case R.id.refresh_button:
                new GetPictureInfo().execute((Void) null);
                break;
            case R.id.imageView:
                new PictureDialog(getContext(),
                        Constants.imageBitmap, titleLabel.getText().toString()).show();
                break;
            case R.id.dress_button:
                showDressCode();
                break;
        }
    }
    private void switchBottomBar(final int bar) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < bottomBars.length; i++) {
                    bottomBars[i].setVisibility(i == bar ? View.VISIBLE : View.GONE);
                }
                if (bar == 1) {
                    String[] data = getData(picNumber);
                    titleLabel.setText(data[0]);
                    showTutorialComment();
                } else if (bar == 0) {
                    titleLabel.setText("Is this Professional?");
                }else{
                    titleLabel.setText("No More Pictures");
                }
            }
        }, 300);
    }

    private void getComment(final Map<String, String> postParams){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){

            }
        });
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText editCommentText = (EditText)
                        ((AlertDialog) dialog).findViewById(R.id.user_comment);

                RatingBar userRating = (RatingBar)
                        ((AlertDialog) dialog).findViewById(R.id.user_rate);

                postParams.put("style", Integer.toString(
                        Math.round(userRating.getRating())));

                String comment = editCommentText.getText().toString().trim();
                if(!comment.equals("")){
                    postParams.put("comment", comment);
                }
                new SubmitRating().execute(postParams);
                dialog.dismiss();
            }
        });
        final AlertDialog dialog = builder.create();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogLayout = inflater.inflate(R.layout.dialog_rate, null);
        dialog.setView(dialogLayout);
        dialog.setTitle("How does my tie look?"); //Question goes here

        dialog.show();
    }

    private Map<String, String> getRateParams(Map<String, String> params, Dialog dialogView) {
        RatingBar style = (RatingBar) dialogView.findViewById(R.id.user_rate);
        int styleRating = Math.round(style.getRating());
        params.put("style", "" + styleRating);
        style.setRating(3);

        EditText commentView = (EditText) rootView.findViewById(R.id.user_comment);

        String comment = commentView.getText().toString();
        Util.log(comment);
        commentView.setText("");
        if(comment.length() > 0)
            params.put("comment", comment);
        return params;
    }

    @Override
    public void onAuthcodeInvalid() {
        Toast.makeText(getContext(), "Please login again", Toast.LENGTH_LONG).show();
        new Logout(getContext(), null);
    }

    @Override
    public void onAuthcodeValid() {

    }

    private class GetPicture extends AsyncTask<Integer, Void, Boolean> {
        SecureAPI picture = SecureAPI.getInstance(getContext());

        @Override
        protected Boolean doInBackground(Integer... params) {
            try{
                int pid = params[0];
                Util.log("picture/" + pid + "?authcode=" + Constants.AUTHCODE);
                picture.HTTPSFETCHPIC("picture/" + pid + "?authcode=" + Constants.AUTHCODE, new File(location, "picture.jpg"));
                if(Constants.imageBitmap != null){
                    Constants.imageBitmap.recycle();
                }
                Constants.imageBitmap = PictureHelper.getPictureBitmap(new File(location, "picture.jpg"));
            }catch(Exception e){
                Util.log(e.getMessage());
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean v) {
            if(v){
                image.setImageBitmap(null);
                image.setImageBitmap(Constants.imageBitmap);
                //ViewGroup.MarginLayoutParams imageViewParams = new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT);
                showProgress(false);
                //image.setScaleType(ImageView.ScaleType.CENTER);
            }else{
                Util.log("Life will go on");
            }
        }
    }

    private class SubmitRating extends AsyncTask<Map<String, String>, Void, Boolean> {
        SecureAPI picture = SecureAPI.getInstance(getContext());
        JSONObject result;

        @SafeVarargs
        @Override
        protected final Boolean doInBackground(Map<String, String>... params) {

            Map<String, String> finalParams = params[0];
            try {
                result = picture.HTTPSPOST("picture/" + finalParams.get("pid")
                        + "/comment?authcode=" + Constants.AUTHCODE, finalParams);
            } catch(Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean v) {
            if(v) {
                Util.log("Rating worked");
                picNumber += 1;
                runFetch(picNumber);

            } else {
                Toast.makeText(rootView.getContext(), "Rating failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private class GetPictureInfo extends AsyncTask<Void, Void, Void> {

        private ArrayList<JSONObject> ret = new ArrayList<>();
        SecureAPI picture = SecureAPI.getInstance(getContext());
        ArrayList<Picture> pictures = new ArrayList<Picture>();


        @Override
        protected Void doInBackground(Void... params) {
            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;


            int dist = 10000;
            int view = 15;



            Util.log(Constants.LATITUDE + " " + Constants.LONGITUDE);

            try {
                JSONObject response = picture.HTTPSGET(Commands.Get.FETCH + Constants.AUTHCODE + "&ft_me=1" + "&ft_views=" + view
                        + "&geolong=" + Constants.LONGITUDE + "&geolat=" + Constants.LATITUDE + "&ft_dist=" + dist);

                JSONArray array = response.getJSONArray("data");

                //debug added actual locations
                Constants.LATITUDE = simpleLocation.getLatitude();
                Constants.LONGITUDE = simpleLocation.getLongitude();



                for(int i = 0; i < array.length(); i++ ){

                    int views = array.getJSONObject(i).getInt("views");
                    int pid = array.getJSONObject(i).getInt("pid");
                    double geolat =  array.getJSONObject(i).getDouble("geolat");
                    double geolong = array.getJSONObject(i).getDouble("geolong");
                    double distance =  Math.round(array.getJSONObject(i).getDouble("dist"));
                    String title = array.getJSONObject(i).getString("title");
                    String username = array.getJSONObject(i).getString("username");
                    String created = array.getJSONObject(i).getString("created");

                    //Calculates priority
                    int p;
                    if(views <= 10)
                        p = (int) ((30 * Math.log(11 - views))/(Math.log(11)) + 40);
                    else
                        p = 30/(views - 10);

                    SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");


                    Date d = simpleDateFormat2.parse(array.getJSONObject(i)
                            .getString("created"));

                    TimeZone tz = TimeZone.getDefault();
                    Util.log(tz.getDisplayName());

                    int hours = (tz.getOffset(d.getTime()) + tz.getDSTSavings()) / 1000 / 60 / 60;
                    Util.log("" + hours);



                    long different = new Date().getTime() -
                            d.getTime();
                    long elapsedHours = (different / (1000 * 60 * 60)) - hours;

                    if(elapsedHours < 10) {
                        p += 3 * elapsedHours;
                    } else {
                        p += 30;
                    }
                    double priority = p;
                    Util.log("" + distance);
                    pictures.add(new Picture(pid, geolat, geolong, created, title, username, priority, (double) elapsedHours, distance));

                }
            }catch (Exception e){
                if(Constants.DEBUG_MODE){
                    Util.log(e.getMessage());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            Util.log("Finished getting Picture Information");
            Collections.sort(pictures, new Comparator<Picture>() {
                public int compare(Picture p1, Picture p2) {
                    return (int) (p2.priority - p1.priority);
                }
            });

            pics = pictures;
            runOnce = pics.size() > 0;
            picNumber = 0;
            runFetch(picNumber);
        }
    }
    public void hideKeyboard(){
        try {
            InputMethodManager inputManager = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(
                    getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }catch (Exception ignored){

        }
    }

    private void showProgress(Boolean show){
        evalContent.setVisibility(show ? View.GONE : View.VISIBLE);
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
    private void showDressCode(){
        //Create an alert dialog builder for a new alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        //Set the title and message
        builder.setIcon(R.mipmap.ic_launcher)
                .setMessage(Html.fromHtml(getContext().getString(R.string.dress_code_dialog)))
                .setTitle("FBLA Official Dress Code");

        //Add an OK button
        builder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        //Create the actual dialog from the builder, then show it.
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}