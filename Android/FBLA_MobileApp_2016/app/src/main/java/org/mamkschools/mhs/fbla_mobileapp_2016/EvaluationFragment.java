package org.mamkschools.mhs.fbla_mobileapp_2016;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.design.widget.FloatingActionButton;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.mamkschools.mhs.fbla_mobileapp_2016.lib.Constants;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.PictureContract;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.SecureAPI;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.util;

import java.io.File;

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

    private ImageView image;
    private TextView descriptionLabel;
    private TextView titleLabel;

    private View ratingLayout;
    private View buttonLayout;
    private View instructions;

    private Bitmap imageData;
    private Cursor c;


    public static EvaluationFragment newInstance(SQLiteDatabase db, int picNumber, File location) {
        EvaluationFragment fragment = new EvaluationFragment();
        fragment.db = db;
        fragment.picNumber = picNumber;
        fragment.location = location;
        return fragment;
    }

    public EvaluationFragment() {}



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_activity_swipes, container, false);
        titleLabel = (TextView) rootView.findViewById(R.id.title_label);
        titleLabel.setText("No More Pictures");


        ratingLayout = rootView.findViewById(R.id.ratingLayout);
        buttonLayout = rootView.findViewById(R.id.buttonLayout);
        instructions = rootView.findViewById(R.id.instructions);

        View imageFrame = rootView.findViewById(R.id.imageFrame);
       // imageFrame.getLayoutParams().height = imageFrame.getLayoutParams().width;
        //util.log(""+ imageFrame.getLayoutParams().width);

        image = (ImageView) rootView.findViewById(R.id.imageView);
        descriptionLabel = (TextView) rootView.findViewById(R.id.description_label);


        FloatingActionButton yes = (FloatingActionButton) rootView.findViewById(R.id.yesButton);
        yes.setOnClickListener(this);

        FloatingActionButton no = (FloatingActionButton) rootView.findViewById(R.id.noButton);
        no.setOnClickListener(this);

        Button submit = (Button) rootView.findViewById(R.id.submit_button);
        submit.setOnClickListener(this);
        
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
            /*try {
                if (imageData != null && !imageData.isRecycled()){
                    imageData.recycle();
                }
                imageData = getPictureBitmap(new File(location, "picture.jpg"));
                image.setImageBitmap(imageData);
            }catch(Exception e){
                image.setImageDrawable(Drawable.createFromPath("@drawable/cslogo"));
            }*/
        }

        return rootView;
    }
    public Bitmap getPictureBitmap(File file){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        return bitmap;

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
            //image.setImageDrawable(Drawable.createFromPath("@drawable/cslogo"));
            refreshView();
        }
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.submit_button:
                this.picNumber += 1;
                runFetch(picNumber);
                break;
        }

        if(ratingLayout.getVisibility() == View.INVISIBLE) {
            ratingLayout.setVisibility(View.VISIBLE);
            titleLabel.setVisibility(View.INVISIBLE);
            buttonLayout.setVisibility(View.INVISIBLE);
            instructions.setVisibility(View.INVISIBLE);
        } else {
            ratingLayout.setVisibility(View.INVISIBLE);
            titleLabel.setVisibility(View.VISIBLE);
            buttonLayout.setVisibility(View.VISIBLE);
            instructions.setVisibility(View.VISIBLE);
        }
    }

    private void refreshView(){
        FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
        fragTransaction.detach(this);
        fragTransaction.attach(this);
        fragTransaction.commit();
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
                if (imageData != null){
                    imageData.recycle();
                }
                imageData = null;
                imageData = getPictureBitmap(new File(location, "picture.jpg"));

            }catch(Exception e){
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean v) {
            if(v){
                util.log("YAY");
                image.setImageBitmap(imageData);

                //refreshView();
            }else{
                util.log("Life will go on");
            }
        }
    }

}
/*
int pid = array.getJSONObject(i).getInt("pid");
File file = picture.HTTPSFETCHPIC("picture/" + pid + "?authcode=" + Constants.AUTHCODE, new File(getFilesDir(), "picture" + pid + ".jpg"));
 */