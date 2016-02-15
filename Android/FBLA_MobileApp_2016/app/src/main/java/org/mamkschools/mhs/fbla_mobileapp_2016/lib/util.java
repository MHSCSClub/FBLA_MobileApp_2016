package org.mamkschools.mhs.fbla_mobileapp_2016.lib;

import android.util.Log;

/**
 * Created by Andrew on 2/14/2016.
 */
public class util {

    //Debug string to identify in Log
    private static final String DEBUG = "FBLA_MobileApp_2016-DBG";


    public static void log(String message){
        if(Constants.DEBUG_MODE){
            Log.d(DEBUG, message);
        }
    }

    public static void load_prefs(){
        //TODO: Load preferences
    }

    public static void save_prefs(){
        //TODO: Save preferences
    }
}
