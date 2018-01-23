package com.example.luc11u.sam;

import android.provider.BaseColumns;

/**
 * Created by luc11u on 22-Jan-18.
 */

public final class SAMDBEntries {

    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "site";
        public static final String COLUMN_NAME_NAME = "nom";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "logitude";
        public static final String COLUMN_NAME_SUMMARY = "resume";
        public static final String COLUMN_NAME_ADRESS = "adresse";
        public static final String COLUMN_NAME_CATEGORY = "categorie";
    }

}
