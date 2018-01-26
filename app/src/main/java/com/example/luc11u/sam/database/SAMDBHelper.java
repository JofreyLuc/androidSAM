package com.example.luc11u.sam.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// Database helper used to access the db
public class SAMDBHelper extends SQLiteOpenHelper {

    // Database version, changes on upgrade
    public static int DATABASE_VERSION = 1;

    // Database global name
    public static final String DATABASE_NAME = "SAMDB";

    // "site" table creation query
    private static final String TABLESITE_CREATION_QUERY =
            "CREATE TABLE " + SAMDBEntries.FeedEntry.TABLE_NAME + " (" +
                    SAMDBEntries.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    SAMDBEntries.FeedEntry.COLUMN_NAME_NAME + " TEXT," +
                    SAMDBEntries.FeedEntry.COLUMN_NAME_LATITUDE + " REAL," +
                    SAMDBEntries.FeedEntry.COLUMN_NAME_LONGITUDE + " REAL," +
                    SAMDBEntries.FeedEntry.COLUMN_NAME_ADRESS + " TEXT," +
                    SAMDBEntries.FeedEntry.COLUMN_NAME_CATEGORY + " TEXT," +
                    SAMDBEntries.FeedEntry.COLUMN_NAME_SUMMARY + " TEXT)";

    // Query used to drop all the db tables
    private static final String DATABASE_DELETE_QUERY =
            "DROP TABLE IF EXISTS " + SAMDBEntries.FeedEntry.TABLE_NAME;

    // Contructor which only calls its super
    public SAMDBHelper(Context c) {
        super(c, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Executes the table creation queries
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TABLESITE_CREATION_QUERY);
    }

    // Drops all the tables and recreates them
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(DATABASE_DELETE_QUERY);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(DATABASE_DELETE_QUERY);
        onCreate(sqLiteDatabase);
    }
}
