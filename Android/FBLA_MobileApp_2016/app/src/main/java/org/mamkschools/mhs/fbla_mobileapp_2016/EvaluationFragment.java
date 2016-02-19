package org.mamkschools.mhs.fbla_mobileapp_2016;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.design.widget.FloatingActionButton;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.Constants;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.PictureContract;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.SecureAPI;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import im.delight.android.location.SimpleLocation;

/**
 * Created by jackphillips on 2/16/16.
 */
public class EvaluationFragment extends Fragment implements View.OnClickListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    private SQLiteDatabase db;
    private int picNumber;
    private File location;
    private SimpleLocation simpleLocation;

    private View rootView;

    private ImageView image;
    private TextView descriptionLabel;
    private TextView titleLabel;
    private View additionalLabel;

    private View ratingLayout;
    private View buttonLayout;
    private View instructions;

    private Bitmap imageData;

    private int currentRating;

    private Cursor c;


    public static EvaluationFragment newInstance(SQLiteDatabase db, int picNumber, File location, SimpleLocation simpleLocation) {
        EvaluationFragment fragment = new EvaluationFragment();
        fragment.db = db;
        fragment.picNumber = picNumber;
        fragment.location = location;
        fragment.simpleLocation = simpleLocation;
        return fragment;
    }

    public EvaluationFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main_activity_swipes, container, false);
        titleLabel = (TextView) rootView.findViewById(R.id.title_label);
        titleLabel.setText("No More Pictures"); //TODO use Strings xml


        ratingLayout = rootView.findViewById(R.id.ratingLayout);
        buttonLayout = rootView.findViewById(R.id.buttonLayout);
        instructions = rootView.findViewById(R.id.instructions);
        additionalLabel = rootView.findViewById(R.id.additional_label);

        View imageFrame = rootView.findViewById(R.id.imageFrame);

        image = (ImageView) rootView.findViewById(R.id.imageView);
        descriptionLabel = (TextView) rootView.findViewById(R.id.description_label);


        FloatingActionButton yes = (FloatingActionButton) rootView.findViewById(R.id.yesButton);
        yes.setOnClickListener(this);

        FloatingActionButton no = (FloatingActionButton) rootView.findViewById(R.id.noButton);
        no.setOnClickListener(this);

        Button submit = (Button) rootView.findViewById(R.id.submit_button);
        submit.setOnClickListener(this);

        Button cancel = (Button) rootView.findViewById(R.id.cancel_button);
        cancel.setOnClickListener(this);
        
        c = getInfo();
        runFetch(picNumber);

        if(c.getCount() > 0 && picNumber < c.getCount()) {
            util.log("From: " + picNumber);
            c.moveToPosition(picNumber);
            int itemId = c.getInt(c.getColumnIndexOrThrow(PictureContract.PictureEntry.COLUMN_NAME_PICTURE_ID));
            String title = c.getString(c.getColumnIndexOrThrow(PictureContract.PictureEntry.COLUMN_NAME_TITLE));
            String user = c.getString(c.getColumnIndexOrThrow(PictureContract.PictureEntry.COLUMN_NAME_USERNAME));
            int views = c.getInt(c.getColumnIndexOrThrow(PictureContract.PictureEntry.COLUMN_NAME_VIEWS));
            titleLabel.setText(title.length() > 20 ? title.substring(0, 20) : title);
            descriptionLabel.setText(user);
        }

        return rootView;
    }
    public Bitmap getPictureBitmap(File file){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inMutable = true;
        if (imageData != null) {
            options.inBitmap = imageData;
        }
        try {
            Bitmap b = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            util.log("" + b.getByteCount()); //do not remove line throws exception if decoding problem
            return b;
        } catch(Exception e) {
            //Problem decoding into existing bitmap, allocate new memory
            options.inBitmap = null;
            return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        }

    }
    public int getPictureId(int picture){
        if(c.getCount() > 0 && picture < c.getCount()) {
            c.moveToPosition(picture);
            return c.getInt(c.getColumnIndexOrThrow(PictureContract.PictureEntry.COLUMN_NAME_PICTURE_ID));
        }
        return -1;

    }

    public String[] getData(int picture){
        String[] data = new String[2];
        if(c.getCount() > 0 && picture < c.getCount()) {
            c.moveToPosition(picture);
            data[0] = c.getString(c.getColumnIndexOrThrow(PictureContract.PictureEntry.COLUMN_NAME_TITLE));
            data[1] = c.getString(c.getColumnIndexOrThrow(PictureContract.PictureEntry.COLUMN_NAME_USERNAME));
            return data;
        }
        data[0] = "No More Pictures";
        data[1] = "";
        return data;
    }

    public void runFetch(int itemId) {
        int picID = getPictureId(itemId);
        String[] data = getData(itemId);
        titleLabel.setText(data[0].length() > 20 ? data[0].substring(0, 20) : data[0]);
        descriptionLabel.setText(data[1]);
        if(picID > 0) {
            new GetPicture().execute(picID);
        }else{
            image.setImageDrawable(getResources().getDrawable(R.drawable.finish));
            new GetPictureInfo().execute((Void) null);
        }
    }
    @Override
    public void onClick(View v) {
        Map<String, String> postParams = new HashMap<>();
        postParams.put("pid", "" + getPictureId(picNumber));
        postParams.put("like", "" + currentRating);

        switch(v.getId()){
            case R.id.yesButton:
                currentRating = 1;
                break;
            case R.id.noButton:
                currentRating = 0;
                break;

            case R.id.cancel_button:
                new SubmitRating().execute(postParams);
                this.picNumber += 1;
                runFetch(picNumber);
                break;

            case R.id.submit_button:
                new SubmitRating().execute(getRateParams(postParams));
                this.picNumber += 1;
                runFetch(picNumber);
                break;
        }

        if(ratingLayout.getVisibility() == View.INVISIBLE) {
            ratingLayout.setVisibility(View.VISIBLE);
            additionalLabel.setVisibility(View.INVISIBLE);
            buttonLayout.setVisibility(View.INVISIBLE);
            instructions.setVisibility(View.INVISIBLE);
        } else {
            ratingLayout.setVisibility(View.INVISIBLE);
            additionalLabel.setVisibility(View.VISIBLE);
            buttonLayout.setVisibility(View.VISIBLE);
            instructions.setVisibility(View.VISIBLE);
        }
    }

    private Map<String, String> getRateParams(Map<String, String> params) {
        SeekBar style = (SeekBar) rootView.findViewById(R.id.styleRating);
        int srating = (int) Math.round((double) style.getProgress() / (double) style.getMax() * 10.0);
        params.put("style", "" + srating);

        EditText commentView = (EditText) rootView.findViewById(R.id.commentText);
        String comment = commentView.getText().toString();
        util.log(comment);
        if(comment.length() > 0)
            params.put("comment", comment);
        return params;
    }

    public Cursor getInfo(){
        String[] projection = {
                PictureContract.PictureEntry._ID,
                PictureContract.PictureEntry.COLUMN_NAME_PICTURE_ID,
                PictureContract.PictureEntry.COLUMN_NAME_GEOLONG,
                PictureContract.PictureEntry.COLUMN_NAME_GEOLAT,
                PictureContract.PictureEntry.COLUMN_NAME_USERNAME,
                PictureContract.PictureEntry.COLUMN_NAME_VIEWS,
                PictureContract.PictureEntry.COLUMN_NAME_TITLE
        };

        String sortOrder =
                PictureContract.PictureEntry.COLUMN_NAME_PICTURE_ID + " ASC";

        return db.query(
                PictureContract.PictureEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
    }
    private class GetPicture extends AsyncTask<Integer, Void, Boolean> {
        SecureAPI picture = SecureAPI.getInstance(getContext());

        @Override
        protected Boolean doInBackground(Integer... params) {
            try{
                int pid = params[0];
                picture.HTTPSFETCHPIC("picture/" + pid + "?authcode=" + Constants.AUTHCODE, new File(location, "picture.jpg"));
                imageData = getPictureBitmap(new File(location, "picture.jpg"));
            }catch(Exception e){
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean v) {
            if(v){
                image.setImageBitmap(imageData);
            }else{
                util.log("Life will go on");
            }
        }
    }

    private class SubmitRating extends AsyncTask<Map<String, String>, Void, Boolean> {
        SecureAPI picture = SecureAPI.getInstance(getContext());
        JSONObject result;

        @Override
        protected Boolean doInBackground(Map<String, String>... params) {

            Map<String, String> finalParams = params[0];
            try {
                result = picture.HTTPSPOST("picture/" + finalParams.get("pid") + "/comment?authcode=" + Constants.AUTHCODE, finalParams);
            } catch(Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean v) {
            if(v) {

            } else {
                Toast.makeText(rootView.getContext(), "Rating failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private class GetPictureInfo extends AsyncTask<Void, Void, Void> {

        private ArrayList<JSONObject> ret = new ArrayList<>();
        SecureAPI picture = SecureAPI.getInstance(getContext());


        @Override
        protected Void doInBackground(Void... params) {

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;


            int amount = 25;
            int dist = 10000;

            //debug added actual locations
            Constants.LATITUDE = simpleLocation.getLatitude();
            Constants.LONGITUDE = simpleLocation.getLongitude();

            db.execSQL("Delete from " + PictureContract.PictureEntry.TABLE_NAME);
            ContentValues values = new ContentValues();

            util.log(Constants.LATITUDE + " " + Constants.LONGITUDE);

            try {
                JSONObject response = picture.HTTPSGET("picture/fetch?authcode=" + Constants.AUTHCODE
                        + "&geolong=" + Constants.LONGITUDE + "&geolat=" + Constants.LATITUDE + "&amount="
                        + amount + "&ft_dist=" + dist);

                JSONArray array = response.getJSONArray("data");
                for(int i = 0; i < array.length(); i++ ){
                    int views = array.getJSONObject(i).getInt("views");
                    values.put(PictureContract.PictureEntry.COLUMN_NAME_PICTURE_ID, array.getJSONObject(i).getInt("pid"));
                    values.put(PictureContract.PictureEntry.COLUMN_NAME_GEOLAT, array.getJSONObject(i).getDouble("geolat"));
                    values.put(PictureContract.PictureEntry.COLUMN_NAME_GEOLONG, array.getJSONObject(i).getDouble("geolong"));
                    values.put(PictureContract.PictureEntry.COLUMN_NAME_DIST, Math.round(array.getJSONObject(i).getDouble("dist")));
                    values.put(PictureContract.PictureEntry.COLUMN_NAME_TITLE, array.getJSONObject(i).getString("title"));
                    values.put(PictureContract.PictureEntry.COLUMN_NAME_USERNAME, array.getJSONObject(i).getString("username"));
                    values.put(PictureContract.PictureEntry.COLUMN_NAME_VIEWS, views);
                    values.put(PictureContract.PictureEntry.COLUMN_NAME_CREATED, array.getJSONObject(i).getString("created"));
                    //Calculates priority
                    int p;
                    if(views <10)
                        p = (int) ((30 * Math.log(11 - views))/(Math.log(11)) + 40);
                    else
                        p = 30/(views - 10);

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    long different = new Date().getTime() - simpleDateFormat.parse(array.getJSONObject(i).getString("created")).getTime();
                    long elapsedHours = different / hoursInMilli;
                    if(elapsedHours < 10)
                        p += 3*elapsedHours;
                    else
                        p+= 30;
                    values.put(PictureContract.PictureEntry.COLUMN_NAME_HOURS, elapsedHours);
                    values.put(PictureContract.PictureEntry.COLUMN_NAME_PRIORITY, p);

                    //adds only pictures we want to server
                    if(views < 15 && elapsedHours < 120) {
                        long newRowId;
                        newRowId = db.insert(
                                PictureContract.PictureEntry.TABLE_NAME,
                                "null",
                                values);
                    }

                }
            }catch (Exception e){
                if(Constants.DEBUG_MODE){
                    util.log(e.getMessage());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            util.log("Finished getting Picture Infomation");
            c = getInfo();
            picNumber = 0;
            runFetch(picNumber);

        }
    }

}