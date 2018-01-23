package com.example.luc11u.sam;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.luc11u.sam.model.Site;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class SAMActivity extends FragmentActivity implements OnMapReadyCallback, OnPositionToRetrieve {
    final DBManagement db = new DBManagement();
    final Map map = new Map();
    private GoogleMap mMap;
    private Location currentLocation;
    private ArrayList<Site> sitesToMark;
    private ArrayList<Marker> markers;
    private SAMDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sam);

        sitesToMark = new ArrayList<>();
        markers = new ArrayList<>();

        dbHelper = new SAMDBHelper(this);

        updateSitesList();

        Button mapButton = findViewById(R.id.mapButton);
        Button dbButton = findViewById(R.id.dbButton);

        dbButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager m = getSupportFragmentManager();
                FragmentTransaction ft = m.beginTransaction();

                ft.replace(R.id.contentView, db);
                ft.addToBackStack(null);

                ft.commit();
            }
        });

        mapButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSitesList();
                updateMarkerList();
                FragmentManager m = getSupportFragmentManager();
                FragmentTransaction ft = m.beginTransaction();


                ft.replace(R.id.contentView, map);
                ft.addToBackStack(null);

                ft.commit();

                markMap();
            }
        });

        FragmentManager m = getSupportFragmentManager();
        FragmentTransaction ft = m.beginTransaction();
        ft.add(R.id.contentView, map);
        ft.commit();




        LocationListener locLis = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (mMap != null) {
                    try {
                        if (mMap.isMyLocationEnabled()) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                            currentLocation = location;
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                            markMap();
                        } else {
                            mMap.setMyLocationEnabled(true);
                            currentLocation = location;
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                            markMap();
                        }
                    } catch (SecurityException e){
                        Toast toast = Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        LocationManager locM = (LocationManager) getSystemService(LOCATION_SERVICE);

        try {
            locM.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, locLis);
        } catch (SecurityException e){
            Toast toast = Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }


    }

    private void updateSitesList(){
        sitesToMark.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
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
            sitesToMark.add(s);
        }
        db.close();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        updateSitesList();
        updateMarkerList();
    }

    private void updateMarkerList(){
        markers.clear();
        for (Site s : sitesToMark) {
            Marker newM = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(s.getLatitude(), s.getLongitude()))
                                .title(s.getNom()));
            markers.add(newM);
            newM.setVisible(false);
        }
    }

    private void markMap() {
        Log.d("AZER", "markmap");
        for (Marker m : markers) {
            Log.d("AZER", m.getTitle());
            float[] distance = new float[1];
            Location.distanceBetween(m.getPosition().latitude, m.getPosition().longitude,
                    currentLocation.getLatitude(), currentLocation.getLongitude(), distance);
            if (distance[0] <= 200) {
                m.setVisible(true);
            } else {
                m.setVisible(false);
            }
        }
    }

    @Override
    public LatLng sendPosition() {
        if (currentLocation != null) {
            return new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        } else {
            return null;
        }
    }

    public SAMDBHelper getDBHelper(){
        return dbHelper;
    }
}
