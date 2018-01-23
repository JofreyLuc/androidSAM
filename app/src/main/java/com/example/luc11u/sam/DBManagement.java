package com.example.luc11u.sam;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.luc11u.sam.model.Site;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


public class DBManagement extends Fragment implements OnSiteToAdd, OnSiteToDelete {

    private DBSitesList siteList;
    private DBAddSite addSite;
    private SAMDBHelper dbHelper;
    private OnPositionToRetrieve positionGiver;


    public DBManagement() {
        // Required empty public constructor
    }

    public void onAttachToParentActivity(Activity a){
        try {
            positionGiver = (OnPositionToRetrieve)a;
        } catch (ClassCastException e){
                throw new ClassCastException(
                        "Parent activity must implement OnPositionToRetrieve : " + e.getLocalizedMessage());
            }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dbmanagement, container, false);

        onAttachToParentActivity(getActivity());

        siteList = (DBSitesList) getChildFragmentManager().findFragmentById(R.id.fragment_siteList);
        addSite = (DBAddSite) getChildFragmentManager().findFragmentById(R.id.fragment_addSite);

        dbHelper = ((SAMActivity)getActivity()).getDBHelper();

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.close();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateDbView();
    }

    private void updateDbView() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(SAMDBEntries.FeedEntry.TABLE_NAME, null, null, null, null, null, null);

        ArrayList<Site> sites = new ArrayList<>();
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
        siteList.updateListView(sites);

        db.close();
    }

    @Override
    public void addSite(String name, String category, String adress, String summary) {
        LatLng pos = positionGiver.sendPosition();
        if (pos == null) {
            Toast.makeText(getActivity().getApplicationContext(), "Impossible de récupérer la position actuelle.", Toast.LENGTH_SHORT);
        } else {
            addSiteToDb(name, adress, category, summary, pos.latitude, pos.longitude);
        }
    }

    private void addSiteToDb(String name, String adress, String category, String summary, double lati, double longi){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SAMDBEntries.FeedEntry.COLUMN_NAME_NAME, name);
        values.put(SAMDBEntries.FeedEntry.COLUMN_NAME_ADRESS, adress);
        values.put(SAMDBEntries.FeedEntry.COLUMN_NAME_CATEGORY, category);
        values.put(SAMDBEntries.FeedEntry.COLUMN_NAME_SUMMARY, summary);
        values.put(SAMDBEntries.FeedEntry.COLUMN_NAME_LATITUDE, lati);
        values.put(SAMDBEntries.FeedEntry.COLUMN_NAME_LONGITUDE, longi);

        db.insert(SAMDBEntries.FeedEntry.TABLE_NAME, null, values);

        db.close();

        updateDbView();
    }

    @Override
    public void deleteSite(Site s) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String where = SAMDBEntries.FeedEntry._ID + " = " + s.getId();

        db.delete(SAMDBEntries.FeedEntry.TABLE_NAME, where, null);

        db.close();
    }
}
