package org.mamkschools.mhs.fbla_mobileapp_2016.lib;

import android.util.Log;

/**
 * Constants for APP
 * Created by Andrew on 10/16/2015.
 */
public final class Constants {
    //API URL
    public static final String API_BASE_URL = "https://aakatz3.aakportfolio.com:9084/fbla2016/api/";

    //HTTPS API (constant because no need to keep recreating)
    //This will be removed in a later build
    public static SecureAPI HTTPS = null;

    //Debug string to identify in Log
    private static final String DEBUG = "FBLA_MobileApp_2016-DBG";

    //Output debug info
    public static final boolean DEBUG_MODE = true;

    //Show static demos in case of server problems
    public static final boolean DEMO_MODE = false;

    //Authcode needs to be saved and loaded
    public static String AUTHCODE = null;

    private Constants(){
        //Do nothing constructor, exists to defeat instantiation.
    }
    public static void log(String message){
        if(Constants.DEBUG_MODE){
            Log.d(Constants.DEBUG, message);
        }
    }
}
