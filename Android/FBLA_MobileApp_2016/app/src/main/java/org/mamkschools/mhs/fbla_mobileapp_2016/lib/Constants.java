package org.mamkschools.mhs.fbla_mobileapp_2016.lib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Constants for APP
 * Created by Andrew on 10/16/2015.
 */
public final class Constants {
    //API URL
    public static final String API_BASE_URL = "https://aakatz3.aakportfolio.com:9084/fbla2016/api/";

    //Output debug info
    public static boolean DEBUG_MODE = true;

    //Show static demos in case of server problems
    public static boolean DEMO_MODE = false;

    //Authcode needs to be saved and loaded
    public static String AUTHCODE = null;

    //Authcode expiration time, to kick user back to login. We need a "renew" function to extend time
    public static long AUTHCODE_EXP;

    //Variable to see if prefs restored yet
    public static boolean PREFS_RESTORED = false;



    private Constants(){
        //Do nothing constructor, exists to defeat instantiation.
    }

    public static void restorePrefs(Context ctx){
        PREFS_RESTORED = true;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        DEBUG_MODE = prefs.getBoolean("DEBUG_MODE", true);
        DEMO_MODE = prefs.getBoolean("DEMO_MODE", false);
        AUTHCODE = prefs.getString("AUTHCODE", null);
        AUTHCODE_EXP = prefs.getLong("AUTHCODE_EXP", -1);
    }

    @SuppressLint("CommitPrefEdits")
    public static void savePrefs(Context ctx){

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("DEBUG_MODE", DEBUG_MODE);
        editor.putBoolean("DEMO_MODE", DEMO_MODE);
        editor.putString("AUTHCODE", AUTHCODE);
        editor.putLong("AUTHCODE_EXP", AUTHCODE_EXP);


        // Commit the edits! Do not change to "apply" or will not be done because app will close!
        editor.commit();
    }
}
