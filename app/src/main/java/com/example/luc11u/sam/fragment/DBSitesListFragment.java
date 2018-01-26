package com.example.luc11u.sam.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.luc11u.sam.R;
import com.example.luc11u.sam.adapter.SiteListAdapter;
import com.example.luc11u.sam.model.Site;
import com.example.luc11u.sam.parentInterface.OnSiteToDelete;

import java.util.ArrayList;

// Fragment displaying a list of all the sites in the db
public class DBSitesListFragment extends Fragment {

    // Adapter of the listView component
    private SiteListAdapter sla;
    // List of current sites to display
    private ArrayList<Site> sites;
    // Object which will handle the deletion of a site
    private OnSiteToDelete siteDeleter;

    public DBSitesListFragment() {
        // Required empty public constructor
    }

    // Checks if the parent is ables to handle the site deletion
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

        // Attaches the listView adapter to the site view
        sites = new ArrayList<>();
        sla = new SiteListAdapter(this.getContext(), sites, siteDeleter);
        ((ListView) view.findViewById(R.id.listViewSites)).setAdapter(sla);

        return view;
    }


    public void updateListView(ArrayList<Site> s) {
        sites.clear();
        sites.addAll(s);
        sla.notifyDataSetChanged();
    }
}
