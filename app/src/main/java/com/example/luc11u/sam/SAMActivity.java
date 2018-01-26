package com.example.luc11u.sam;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteException;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.luc11u.sam.adapter.InfoMarkerAdapter;
import com.example.luc11u.sam.database.SiteDao;
import com.example.luc11u.sam.fragment.DBManagementFragment;
import com.example.luc11u.sam.fragment.MapFragment;
import com.example.luc11u.sam.model.Site;
import com.example.luc11u.sam.parentInterface.OnDisplayConstraintsChanged;
import com.example.luc11u.sam.parentInterface.OnPositionToRetrieve;
import com.example.luc11u.sam.parentInterface.OnSiteListToRetrieve;
import com.example.luc11u.sam.parentInterface.OnSiteToAddToDB;
import com.example.luc11u.sam.parentInterface.OnSiteToDeleteFromDB;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

// Main activity of the app : creates the two sub fragments and handles the data
public class SAMActivity extends FragmentActivity implements OnMapReadyCallback, OnPositionToRetrieve, OnDisplayConstraintsChanged, OnSiteToAddToDB, OnSiteToDeleteFromDB, OnSiteListToRetrieve {

    // Main fragments of the app
    private final DBManagementFragment dbFragment = new DBManagementFragment();
    private final MapFragment mapFragment = new MapFragment();
    // Current map
    private GoogleMap mMap;
    private Location currentLocation;
    // Handles the access to the device gps
    private LocationManager locM;
    // Listens to the gps position changes
    private LocationListener locLis;

    // Reflects the list of sites in the database
    private ArrayList<Site> sites;
    // Sites which fits in the display constraints
    private ArrayList<Site> sitesToMark;

    // Display constraints (with default values)
    private int currentMaxdistance = 200;
    private String currentCategory = "Tous";

    // Helper allowing db access
    private SiteDao sitesDAO;

    private final int UPDATE_POSITION_DELAY = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sam);

        // Initializes the sites lists
        sites = new ArrayList<>();
        sitesToMark = new ArrayList<>();

        // Initializes the database helper
        sitesDAO = new SiteDao(this);

        // Loads the db site list
        updateSitesList();

        // Attaches listeners to the map and db buttons : switch between the two main fragments
        Button mapButton = findViewById(R.id.mapButton);
        Button dbButton = findViewById(R.id.dbButton);

        dbButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCurrentFragment(dbFragment);
            }
        });

        mapButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSitesList();
                updateSitesToMarkList();
                setCurrentFragment(mapFragment);
            }
        });

        // Loads the map fragment at launch
        setCurrentFragment(mapFragment);

        // Creates the location listener used to update the current position
        locLis = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (mMap != null) {
                    currentLocation = location;
                    updateSitesToMarkList();
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

        // Gets the location manager
        locM = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Checks the permissions to make sure we have access to the gps
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request gps permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            // Attaches the location listener to the manager : requests updates every 1000 ms
            locM.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_POSITION_DELAY, 0, locLis);
        }
    }

    // Loads a main fragment below the two main buttons
    private void setCurrentFragment(Fragment f){
        FragmentManager m = getSupportFragmentManager();
        FragmentTransaction ft = m.beginTransaction();

        ft.replace(R.id.contentView, f);
        ft.addToBackStack(null);

        ft.commit();
    }

    // Checks the results of the permission request; requestUpdates if yes, closes the app if no
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        locM.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_POSITION_DELAY, 0, locLis);
                    } catch (SecurityException e) {
                        Toast.makeText(this, "Erreur permission GPS", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "GPS nécessaire", Toast.LENGTH_LONG).show();
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
                break;
            }
        }
    }

    // Updates the local site list with the contents of the db
    private void updateSitesList(){
        sites.clear();

        sites.addAll(getSites());
    }

    // Updates the list of sites which fits the display constraints
    private void updateSitesToMarkList() {

        // Creates a temporary list
        ArrayList<Site> potentialSitesToMark = new ArrayList<>();

        // Adds all the sites which fits the display constraints to the temporary list
        for (Site s : sites) {
            if (s.getCategorie().equals(currentCategory) || currentCategory.equals("Tous")) {
                Location sLoc = new Location("site");
                sLoc.setLatitude(s.getLatitude());
                sLoc.setLongitude(s.getLongitude());
                if (currentLocation != null && currentLocation.distanceTo(sLoc) <= currentMaxdistance) {
                    potentialSitesToMark.add(s);
                }
            }
        }

        // If the temporary list and the current list differ in size : updates the map
        if (potentialSitesToMark.size() != sitesToMark.size()) {
            sitesToMark = potentialSitesToMark;
            markMap();
            return;
        }

        // If the temporary list and the current list differ in content : updates the map
        for (Site s : potentialSitesToMark) {
            if (!sitesToMark.contains(s)){
                sitesToMark = potentialSitesToMark;
                markMap();
                return;
            }
        }
    }

    // Displays all of the marked sites on the map
    private void markMap() {
        mMap.clear();
        for (Site s : sitesToMark) {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(s.getLatitude(), s.getLongitude()))
                    .title(s.getNom())
                    .snippet(s.getCategorie() + "\n" + s.getAdresse()));
        }
    }

    // Called when the map is ready to be displayed
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Attaches the infoAdapter (info displayed on the markers)
        mMap.setInfoWindowAdapter(new InfoMarkerAdapter(this));

        // Checks gps permissions and enables the durrent location tracking if ok
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            mMap.setMyLocationEnabled(true);
            // If the gps has a last position registered
            if (locM.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
                zoomTo(locM.getLastKnownLocation(LocationManager.GPS_PROVIDER));
                currentLocation = locM.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }
        updateSitesList();
        updateSitesToMarkList();
    }

    // Zooms to a certain location
    private void zoomTo(Location l) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(l.getLatitude(), l.getLongitude())));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    // Sends the current position
    @Override
    public LatLng sendPosition() {
        if (currentLocation != null) {
            return new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        } else {
            return null;
        }
    }

    // Updates the current category displayed and the map display
    @Override
    public void updateCategoryConstraint(String c) {
        currentCategory = c;
        updateSitesToMarkList();
    }

    // Updates the current range displayed and the map display
    @Override
    public void updateRangeConstraint(int r) {
        currentMaxdistance = r;
        updateSitesToMarkList();
    }

    // Adds a site to the db
    @Override
    public void addSiteToDB(String name, String category, String adress, String summary, double lati, double longi) {
        try {
            sitesDAO.addOne(name, category, adress, summary, lati, longi);
        } catch (SQLiteException e) {
            Toast.makeText(this, "Impossible d'accéder à la bdd " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Delete a site from the db
    @Override
    public void deleteSiteFromDB(Site s) {
        try {
            sitesDAO.deleteOne(s);
        } catch (SQLiteException e) {
            Toast.makeText(this, "Impossible d'accéder à la bdd " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Get all the sites in the db
    @Override
    public ArrayList<Site> getSites() {
        try {
            return sitesDAO.fetchAll();
        } catch (SQLiteException e) {
            Toast.makeText(this, "Impossible d'accéder à la bdd " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
        return null;
    }
}
