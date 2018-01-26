package com.example.luc11u.sam.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.luc11u.sam.R;
import com.example.luc11u.sam.model.Site;
import com.example.luc11u.sam.parentInterface.OnPositionToRetrieve;
import com.example.luc11u.sam.parentInterface.OnSiteListToRetrieve;
import com.example.luc11u.sam.parentInterface.OnSiteToAdd;
import com.example.luc11u.sam.parentInterface.OnSiteToAddToDB;
import com.example.luc11u.sam.parentInterface.OnSiteToDelete;
import com.example.luc11u.sam.parentInterface.OnSiteToDeleteFromDB;
import com.google.android.gms.maps.model.LatLng;

// Fragment displaying two fragments : one will display the site list, the other an add form
public class DBManagementFragment extends Fragment implements OnSiteToAdd, OnSiteToDelete {

    // Fragments displayed
    private DBSitesListFragment siteList;
    private DBAddSiteFragment addSite;
    // Object which will give us the current position
    private OnPositionToRetrieve positionGiver;
    // Objects used to access the db
    private OnSiteToAddToDB siteAdder;
    private OnSiteToDeleteFromDB siteDeleter;
    private OnSiteListToRetrieve siteListGiver;


    public DBManagementFragment() {
        // Required empty public constructor
    }

    // Defines the parent activity as the object allowing us to do everything
    public void onAttachToParentActivity(Activity a){
        try {
            positionGiver = (OnPositionToRetrieve)a;
            siteAdder = (OnSiteToAddToDB)a;
            siteDeleter = (OnSiteToDeleteFromDB)a;
            siteListGiver = (OnSiteListToRetrieve)a;
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

        siteList = (DBSitesListFragment) getChildFragmentManager().findFragmentById(R.id.fragment_siteList);
        addSite = (DBAddSiteFragment) getChildFragmentManager().findFragmentById(R.id.fragment_addSite);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateDbView();
    }

    // Updates the local site list of the list fragment
    private void updateDbView() {
        siteList.updateListView(siteListGiver.getSites());
    }

    // Calls the siteAdder to add a site to the db
    @Override
    public void addSite(String name, String category, String adress, String summary) {
        LatLng pos = positionGiver.sendPosition();
        if (pos == null) {
            Toast.makeText(getActivity().getApplicationContext(), "Impossible de récupérer la position actuelle.", Toast.LENGTH_SHORT);
        } else {
            siteAdder.addSiteToDB(name, category, adress, summary, pos.latitude, pos.longitude);
            updateDbView();
        }
    }

    // Calls the siteAdder to delete a site from the db
    @Override
    public void deleteSite(Site s) {
        siteDeleter.deleteSiteFromDB(s);
    }
}
