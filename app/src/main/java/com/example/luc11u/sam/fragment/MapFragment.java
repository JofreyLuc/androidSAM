package com.example.luc11u.sam.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.luc11u.sam.R;
import com.example.luc11u.sam.model.Site;
import com.example.luc11u.sam.parentInterface.OnDisplayConstraintsChanged;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

// Fragment displaying the mapFragment and two spinners used to choose which sites to display.
public class MapFragment extends Fragment {

    // Spinners and their adapters
    private Spinner categorySpinner;
    private Spinner rangeSpinner;
    private ArrayAdapter<CharSequence> categoryAdapter;
    private ArrayAdapter<CharSequence> rangeAdapter;

    // Object handling the display constraints values
    private OnDisplayConstraintsChanged constraintsHolder;

    // Object handling the map object
    private OnMapReadyCallback mapHolder;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Called when attached to the parent : checks if its able to register display constraints changes
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            constraintsHolder = (OnDisplayConstraintsChanged)context;
            mapHolder = (OnMapReadyCallback)context;
        } catch (ClassCastException e){
            throw new ClassCastException(
                    "Parent activity must implement OnDisplayConstraintsChanged : " + e.getLocalizedMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        initComponents(view);

        return view;
    }

    // Retrieves components from the view and attaches listeners and adapters to them
    private void initComponents(View view) {
        // Put items into the category spinner
        categorySpinner = (Spinner) view.findViewById(R.id.spinnerCategoryMap);
        categoryAdapter = ArrayAdapter.createFromResource(this.getContext(), R.array.categoriesDisplay_array, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        // Attaches a changeListener to the categories spinner
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                constraintsHolder.updateCategoryConstraint(parent.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Put items into the range spinner
        rangeSpinner = (Spinner) view.findViewById(R.id.spinnerRangeMap);
        rangeAdapter = ArrayAdapter.createFromResource(this.getContext(), R.array.ranges_array, android.R.layout.simple_spinner_item);
        rangeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rangeSpinner.setAdapter(rangeAdapter);

        // Attaches a changeListener to the range spinner
        rangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int r = Integer.parseInt(parent.getSelectedItem().toString());
                constraintsHolder.updateRangeConstraint(r);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Gets the mapFragment fragment and attaches it to the parent activity to react to the map creation
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapHolder);
    }
}
