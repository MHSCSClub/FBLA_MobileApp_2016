package org.mamkschools.mhs.fbla_mobileapp_2016.lib;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Contains simple debugging stuff
 * Created by Andrew on 2/14/2016.
 */
public final class Util {

    //Util string to identify in Log
    private static final String DEBUG = "FBLA_MobileApp_2016-DBG";

    //Map function - maps one range to another range
    public static int map(int x, int in_min, int in_max, int out_min, int out_max){
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    //Log function, for logging data in debug mode
    public static void log(String message){
        if(Constants.DEBUG_MODE){
            Log.d(DEBUG, message);
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
