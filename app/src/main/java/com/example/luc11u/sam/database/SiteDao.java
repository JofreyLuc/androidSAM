package com.example.luc11u.sam.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.luc11u.sam.model.Site;

import java.util.ArrayList;

// Dao handling accesses to ths sqlite db
public class SiteDao {

    SQLiteOpenHelper DBHelper;
    Context context;

    public SiteDao(Context c) {
        DBHelper = new SAMDBHelper(c);
        context = c;
    }

    public ArrayList<Site> fetchAll(){
        ArrayList<Site> sites = new ArrayList<>();

        SQLiteDatabase db = DBHelper.getReadableDatabase();
        Cursor cursor = db.query(SAMDBEntries.FeedEntry.TABLE_NAME, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            Site s = new Site(
                    cursor.getInt(cursor.getColumnIndexOrThrow(SAMDBEntries.FeedEntry._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(SAMDBEntries.FeedEntry.COLUMN_NAME_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(SAMDBEntries.FeedEntry.COLUMN_NAME_ADRESS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(SAMDBEntries.FeedEntry.COLUMN_NAME_CATEGORY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(SAMDBEntries.FeedEntry.COLUMN_NAME_SUMMARY)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(SAMDBEntries.FeedEntry.COLUMN_NAME_LATITUDE)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(SAMDBEntries.FeedEntry.COLUMN_NAME_LONGITUDE))
            );
            sites.add(s);
        }
        cursor.close();
        db.close();

        return sites;
    }

    public void addOne(String name, String category, String adress, String summary, double lati, double longi){
        SQLiteDatabase db = DBHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SAMDBEntries.FeedEntry.COLUMN_NAME_NAME, name);
        values.put(SAMDBEntries.FeedEntry.COLUMN_NAME_ADRESS, adress);
        values.put(SAMDBEntries.FeedEntry.COLUMN_NAME_CATEGORY, category);
        values.put(SAMDBEntries.FeedEntry.COLUMN_NAME_SUMMARY, summary);
        values.put(SAMDBEntries.FeedEntry.COLUMN_NAME_LATITUDE, lati);
        values.put(SAMDBEntries.FeedEntry.COLUMN_NAME_LONGITUDE, longi);

        db.insert(SAMDBEntries.FeedEntry.TABLE_NAME, null, values);

        db.close();
    }

    public void deleteOne(Site s){
        SQLiteDatabase db = DBHelper.getWritableDatabase();

        String where = SAMDBEntries.FeedEntry._ID + " = " + s.getId();

        db.delete(SAMDBEntries.FeedEntry.TABLE_NAME, where, null);

        db.close();
    }
}
