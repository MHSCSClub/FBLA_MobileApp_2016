package org.mamkschools.mhs.fbla_mobileapp_2016;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONObject;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.*;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import im.delight.android.location.SimpleLocation;

import static org.mamkschools.mhs.fbla_mobileapp_2016.lib.PictureContract.*;

public class MainActivitySwipes extends AppCompatActivity implements View.OnClickListener {

    private static File location;

    private SimpleLocation Simplocation;

    private MyPagerAdapter pagerAdapter;
    private ViewPager viewPager;

    //Fragments
    private Fragment evaluatePictures;
    private Fragment myPictures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Must be first
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_swipes);

        Simplocation = new SimpleLocation(this);

        // if we can't access the location yet
        if (!Simplocation.hasLocationEnabled()) {
            // ask the user to enable location access
            SimpleLocation.openSettings(this);
        }

        if(!Constants.PREFS_RESTORED){
            Constants.restorePrefs(getApplicationContext());
        }

        //PagerAdapter
        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());

        //ViewPager
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(pagerAdapter);

        //Tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setTabsFromPagerAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        PictureHelper mDbHelper = new PictureHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int picture = 0;

        location = getFilesDir();
        evaluatePictures = EvaluationFragment.newInstance(db, picture, location, Simplocation);
        myPictures = MeFragmentBetter.newInstance(location);

        ActionBar ab = getSupportActionBar();
        
        ab.show();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }




    @Override
    protected void onResume(){
        super.onResume();
        if(Constants.AUTHCODE == null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        Constants.restorePrefs(getApplicationContext());
    }

    @Override
    protected void onStop(){
        super.onStop();
        Constants.PREFS_RESTORED = false;
        Constants.savePrefs(getApplicationContext());
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

        switch (id){
            case R.id.action_settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                return true;
            case R.id.action_logout:
                new Logout().execute((Void) null);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fab:
                //Photo chooser (include default, cam, file (see Google Inbox on Android))
                //openImageIntent();
                break;
            default:
                Toast.makeText(this, "No Assigned Action", Toast.LENGTH_SHORT).show();

        }


    }


    /**
     * A placeholder fragment containing a simple view.
     */



    private class MyPagerAdapter extends FragmentStatePagerAdapter {

        private String[] titles = {"Evaluate", "Me"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {
                case 0: return evaluatePictures;
                case 1: return myPictures;
                default: return evaluatePictures;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }


    private class Logout extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void ... v) {
            SecureAPI HTTPS = SecureAPI.getInstance(getApplicationContext());

            try {
                HTTPS.HTTPSGET(Commands.Get.LOGOUT + Constants.AUTHCODE);
            } catch (Exception ex) {
                util.log(ex.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            Constants.AUTHCODE = null;
            Constants.savePrefs(getApplicationContext());
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
    }


}
