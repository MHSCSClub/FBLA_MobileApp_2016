package org.mamkschools.mhs.fbla_mobileapp_2016;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.*;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.mamkschools.mhs.fbla_mobileapp_2016.lib.PictureContract.*;

public class MainActivitySwipes extends AppCompatActivity implements View.OnClickListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private static File location;
    private static int picture = 0;
    private double geoLong;
    private double geoLat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_activity_swipes);


       /* if(Constants.AUTHCODE == null || System.currentTimeMillis() >= Constants.AUTHCODE_EXP){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }*/




        GetPicture picupload = new GetPicture();
        picupload.execute((Void) null);
        location = getFilesDir();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        Button yes = (Button) findViewById(R.id.yesButton);
        yes.setOnClickListener(this);

        Button no = (Button) findViewById(R.id.noButton);
        no.setOnClickListener(this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);







    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity_swipes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fab:
                if(false) {
                    //Temporary code to prevent crash
                    Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    //TODO Photo chooser (include default, cam, file (see Google Inbox on Android))
                    openImageIntent();
                }
                break;
            case R.id.yesButton:
                picture += 1;
                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
                mViewPager.setAdapter(mSectionsPagerAdapter);
                break;
            case R.id.noButton:
                picture += 1;
                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
                mViewPager.setAdapter(mSectionsPagerAdapter);
                break;
            default:
                Toast.makeText(this, "No Assigned Action", Toast.LENGTH_SHORT).show();

        }


    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //System.out.println(position);
            PictureHelper mDbHelper = new PictureHelper(getApplicationContext());

            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            //mDbHelper.onUpgrade(db, 1, 2);
            return PlaceholderFragment.newInstance(position, db);

        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Section_" + position;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        private static SQLiteDatabase db;
        public static PlaceholderFragment newInstance(int sectionNumber, SQLiteDatabase db1) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            db = db1;
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);

            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {}


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            if(getArguments().getInt(ARG_SECTION_NUMBER) == 0) {
                String[] projection = {
                        PictureEntry._ID,
                        PictureEntry.COLUMN_NAME_PICTURE_ID,
                        PictureEntry.COLUMN_NAME_GEOLONG,
                        PictureEntry.COLUMN_NAME_GEOLAT,
                        PictureEntry.COLUMN_NAME_USERNAME,
                        PictureEntry.COLUMN_NAME_VIEWS,
                        PictureEntry.COLUMN_NAME_TITLE
                };

                String sortOrder =
                        PictureEntry.COLUMN_NAME_PICTURE_ID + " ASC";

                Cursor c = db.query(
                        PictureEntry.TABLE_NAME,  // The table to query
                        projection,                               // The columns to return
                        null,                                // The columns for the WHERE clause
                        null,                            // The values for the WHERE clause
                        null,                                     // don't group the rows
                        null,                                     // don't filter by row groups
                        sortOrder                                 // The sort order
                );
                View rootView = inflater.inflate(R.layout.fragment_main_activity_swipes, container, false);
                TextView textView = (TextView) rootView.findViewById(R.id.section_label);
                textView.setText("No More Pictures");
                ImageView image = (ImageView) rootView.findViewById(R.id.imageView);

                if(c.getCount() > 0 && picture < c.getCount()){
                    c.moveToPosition(picture);
                    int itemId = c.getInt(c.getColumnIndexOrThrow(PictureEntry.COLUMN_NAME_PICTURE_ID));
                    String title = c.getString(c.getColumnIndexOrThrow(PictureEntry.COLUMN_NAME_TITLE));
                    String user = c.getString(c.getColumnIndexOrThrow(PictureEntry.COLUMN_NAME_USERNAME));
                    int views = c.getInt(c.getColumnIndexOrThrow(PictureEntry.COLUMN_NAME_VIEWS));
                    textView.setText(title + "\n" + user + "\n" + "Views: " + views);
                    image.setImageURI(Uri.fromFile(new File(location, "picture" + itemId + ".jpg")));
                }

                return rootView;
            }

            View rootView = inflater.inflate(R.layout.fragment_main_activity_swipes, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }
    private Uri outputFileUri;
    private static final int YOUR_SELECT_PICTURE_REQUEST_CODE = 1;

    private void openImageIntent() {

// Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);
        root.mkdirs();
        final String fname = "img_"+ System.currentTimeMillis() + ".jpg";
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, YOUR_SELECT_PICTURE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == YOUR_SELECT_PICTURE_REQUEST_CODE) {
                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                Uri selectedImageUri;
                if (isCamera) {
                    selectedImageUri = outputFileUri;
                } else {
                    selectedImageUri = data == null ? null : data.getData();
                }
            }
        }
    }
    private class GetPicture extends AsyncTask<Void, Void, Void> {

        private ArrayList<JSONObject> ret = new ArrayList<>();
        SecureAPI picture = SecureAPI.getInstance(MainActivitySwipes.this);

        @Override
        protected Void doInBackground(Void... params) {
            String authCode = "fd60f0349432ef094b75f864495ee25922d87094dd335610dbdb54f4f88ea48b";
            double geolong = -73.748687;
            double geolat = 40.934710;
            int amount = 3;
            int dist = 1000;

            PictureHelper mDbHelper = new PictureHelper(getApplicationContext());
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            db.execSQL("Delete from " + PictureEntry.TABLE_NAME);
            ContentValues values = new ContentValues();



            try {
                JSONObject response = picture.HTTPSGET("picture/fetch?authcode=" + authCode + "&geolong=" + geolong + "&geolat=" + geolat + "&amount=" + amount + "&ft_dist=" + dist);

                JSONArray array = response.getJSONArray("data");
                for(int i = 0; i < array.length(); i++ ){
                    values.put(PictureEntry.COLUMN_NAME_PICTURE_ID, array.getJSONObject(i).getInt("pid"));
                    values.put(PictureEntry.COLUMN_NAME_GEOLAT, array.getJSONObject(i).getDouble("geolat"));
                    values.put(PictureEntry.COLUMN_NAME_GEOLONG, array.getJSONObject(i).getDouble("geolong"));
                    values.put(PictureEntry.COLUMN_NAME_DIST, array.getJSONObject(i).getDouble("dist"));
                    values.put(PictureEntry.COLUMN_NAME_TITLE, array.getJSONObject(i).getString("title"));
                    values.put(PictureEntry.COLUMN_NAME_USERNAME, array.getJSONObject(i).getString("username"));
                    values.put(PictureEntry.COLUMN_NAME_VIEWS, array.getJSONObject(i).getInt("views"));
                    long newRowId;
                    newRowId = db.insert(
                            PictureEntry.TABLE_NAME,
                            "null",
                            values);
                    int pid = array.getJSONObject(i).getInt("pid");
                    File file = picture.HTTPSPIC("picture/" + pid  + "?authcode=" + authCode, new File(getFilesDir(), "picture" + pid + ".jpg"));


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
            util.log("Done With Picture Download");
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
            mViewPager.setAdapter(mSectionsPagerAdapter);



        }
    }
}
