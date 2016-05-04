package org.mamkschools.mhs.fbla_mobileapp_2016.lib;

import android.util.Log;

/**
 * Contains simple debugging stuff
 * Created by Andrew on 2/14/2016.
 */
public final class Util {
    //Output debug info
    public static boolean DEBUG_MODE = false;

    //Util string to identify in Log
    private static final String DEBUG = "FBLA_MobileApp_2016-DBG";

    //Map funciton - maps one range to another range
    public static int map(int x, int in_min, int in_max, int out_min, int out_max){
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    public static void log(String message){
        if(DEBUG_MODE){
            Log.d(DEBUG, message);
        }
    }
}
