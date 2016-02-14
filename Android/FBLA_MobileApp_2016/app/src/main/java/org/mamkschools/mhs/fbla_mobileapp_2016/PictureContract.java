package org.mamkschools.mhs.fbla_mobileapp_2016;

import android.content.Context;
import android.database.sqlite.*;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class PictureContract {
    public PictureContract() {}

    public static abstract class PictureEntry implements BaseColumns {
        public static final String TABLE_NAME = "picture";
        public static final String COLUMN_NAME_PICTURE_ID = "entryid";
        public static final String COLUMN_NAME_GEOLAT = "geolat";
        public static final String COLUMN_NAME_GEOLONG = "geolong";
        public static final String COLUMN_NAME_DIST = "dist";
        public static final String COLUMN_NAME_CREATED = "created";

    }
}
