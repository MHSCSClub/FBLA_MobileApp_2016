package org.mamkschools.mhs.fbla_mobileapp_2016.lib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.File;

/**
 * Contains simple debugging stuff and other util functions
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

    public static Bitmap getPictureBitmap(File file){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inMutable = true;
        options.inScaled = true;
        options.inSampleSize = 1;
        try {
            Bitmap b = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            Util.log("" + b.getByteCount()); //do not remove line throws exception if decoding problem
            return b;
        } catch(Exception | OutOfMemoryError e) {
            //Problem decoding into existing bitmap, allocate new memory
            if(options.inBitmap != null && !options.inBitmap.isRecycled()){
                options.inBitmap.recycle();
            }


            //Fixes out of memory for bitmaps downloaded **Experimental**
            while(true){
                try {
                    return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                } catch (OutOfMemoryError outOfMemoryError){
                    options.inSampleSize *= 2;
                }
            }
        }
    }
}
