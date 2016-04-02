package org.mamkschools.mhs.fbla_mobileapp_2016.lib;

import android.util.Log;

/**
 * Exists to handle all debugging functions/constants
 * Created by Andrew on 2/14/2016.
 */
public class Debug {
    //Output debug info
    public static boolean DEBUG_MODE = true;

    //Debug string to identify in Log
    private static final String DEBUG = "FBLA_MobileApp_2016-DBG";


    public static void log(String message){
        if(DEBUG_MODE){
            Log.d(DEBUG, message);
        }
    }
}
