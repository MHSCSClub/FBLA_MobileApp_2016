package org.mamkschools.mhs.fbla_mobileapp_2016.lib;

/**
 * Constants for APP
 * Created by Andrew on 10/16/2015.
 */
public final class Constants {
    //API URL
    public static final String API_BASE_URL = "https://aakatz3.asuscomm.com:9084/fbla2016/api/";

    //HTTPS API (constant because no need to keep recreating)
    //This will be removed in a later build
    public static SecureAPI HTTPS = null;

    //Debug string to identify in Log
    public static final String DEBUG = "FBLA_MobileApp_2016-DBG";

    private Constants(){
        //Do nothing constructor, exists to defeat instantiation.
    }

}
