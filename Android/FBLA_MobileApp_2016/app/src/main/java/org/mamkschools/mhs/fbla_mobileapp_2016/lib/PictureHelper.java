package org.mamkschools.mhs.fbla_mobileapp_2016.lib;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.mamkschools.mhs.fbla_mobileapp_2016.lib.PictureContract.PictureEntry;

/**
 * Created by jackphillips on 2/13/16.
 */
public class PictureHelper extends SQLiteOpenHelper {
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + PictureEntry.TABLE_NAME + " (" +
                    PictureEntry._ID + " INTEGER PRIMARY KEY," +
                    PictureEntry.COLUMN_NAME_PICTURE_ID + " INTEGER" + COMMA_SEP +
                    PictureEntry.COLUMN_NAME_GEOLAT + " DECIMAL(4,2)" + COMMA_SEP +
                    PictureEntry.COLUMN_NAME_GEOLONG + " DECIMAL(4,2)" + COMMA_SEP +
                    PictureEntry.COLUMN_NAME_DIST  + " DECIMAL(7,12)" +  COMMA_SEP +
                    PictureEntry.COLUMN_NAME_TITLE  + " TEXT" +  COMMA_SEP +
                    PictureEntry.COLUMN_NAME_USERNAME  + " TEXT" +  COMMA_SEP +
                    PictureEntry.COLUMN_NAME_VIEWS + " INTERGER" + COMMA_SEP +
                    PictureEntry.COLUMN_NAME_CREATED + " DATETIME" + COMMA_SEP +
                    PictureEntry.COLUMN_NAME_PRIORITY + " INTEGER" + COMMA_SEP +
                    PictureEntry.COLUMN_NAME_HOURS + " INTEGER" +
                    ")";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + PictureEntry.TABLE_NAME;


    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "picture.db";
    public PictureHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}