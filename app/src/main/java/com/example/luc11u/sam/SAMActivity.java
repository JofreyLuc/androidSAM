package com.example.luc11u.sam;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.luc11u.sam.model.Site;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class SAMActivity extends FragmentActivity implements OnMapReadyCallback, OnPositionToRetrieve {
    final DBManagement db = new DBManagement();
    final Map map = new Map();
    private GoogleMap mMap;
    private Location currentLocation;
    private ArrayList<Site> sites;
    private ArrayList<Site> sitesToMark;
    private ArrayList<Marker> markers;
    private SAMDBHelper dbHelper;
    private LocationManager locM;
    private LocationListener locLis;

    private float CURRENT_MAX_DISTANCE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sam);

        sites = new ArrayList<>();
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
                updateSitesToMarkList();
                markMap();
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

        locLis = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (mMap != null) {
                    zoomTo(location);
                    currentLocation = location;
                    markMap();
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

        locM = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locM.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, locLis);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    try {

                        locM.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, locLis);
                    } catch (SecurityException e) {
                        Toast.makeText(this, "Erreur permission GPS", Toast.LENGTH_LONG);

                    }

                } else {

                    Toast.makeText(this, "GPS nécessaire", Toast.LENGTH_LONG);
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
                return;
            }
        }
    }

    private void updateSitesList(){
        sites.clear();
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
            sites.add(s);
        }
        db.close();
    }

    private void updateSitesToMarkList() {
        sitesToMark.clear();
        for (Site s : sites) {
            //if (s.getCategory() == CURRENT_CATEGORY) {
            //CHECK DISTANCE
//            float[] distance = new float[1];
//            Location.distanceBetween(m.getPosition().latitude, m.getPosition().longitude,
//                    currentLocation.getLatitude(), currentLocation.getLongitude(), distance);
//            if (distance[0] <= CURRENT_MAX_DISTANCE) {
//                m.setVisible(true);
//            } else {
//                m.setVisible(false);
//            }
            sitesToMark.add(s);
            //}
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            mMap.setMyLocationEnabled(true);
            zoomTo(locM.getLastKnownLocation(LocationManager.GPS_PROVIDER));
            currentLocation = locM.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        updateSitesList();
        updateSitesToMarkList();
        markMap();
    }

    private void zoomTo(Location l) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(l.getLatitude(), l.getLongitude())));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    private void markMap() {
        mMap.clear();
        for (Site s : sitesToMark) {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(s.getLatitude(), s.getLongitude()))
                    .title(s.getNom()));
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
