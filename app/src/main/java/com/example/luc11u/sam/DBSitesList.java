package com.example.luc11u.sam;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.luc11u.sam.model.Site;

import java.util.ArrayList;


public class DBSitesList extends Fragment {

    private SiteListAdapter sla;
    private ArrayList<Site> sites;
    private OnSiteToDelete siteDeleter;

    public DBSitesList() {
        // Required empty public constructor
    }

    private void onAttachToFragmentParent(Fragment parent) {
        try {
            siteDeleter = (OnSiteToDelete) parent;
        } catch (ClassCastException e){
            throw new ClassCastException(
                    "Parent fragment must implement OnSiteToDelete : " + e.getLocalizedMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dbsites_list, container, false);

        onAttachToFragmentParent(getParentFragment());

        sites = new ArrayList<>();

        sla = new SiteListAdapter(this.getContext(), sites, siteDeleter);
        ((ListView) view.findViewById(R.id.listViewSites)).setAdapter(sla);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void updateListView(ArrayList<Site> s) {
        sites.clear();
        sites.addAll(s);
        sla.notifyDataSetChanged();
    }
}
