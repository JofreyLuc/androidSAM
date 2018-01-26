package com.example.luc11u.sam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.luc11u.sam.R;
import com.example.luc11u.sam.model.Site;
import com.example.luc11u.sam.parentInterface.OnSiteToDelete;

import java.util.ArrayList;

// Site list view adapter, creates a custom view of an item of the list
public class SiteListAdapter extends BaseAdapter implements ListAdapter {

    // List of sites to display
    private ArrayList<Site> siteList = new ArrayList<>();
    // Parent context
    private Context context;
    // Object which will handle the deletion of a site
    private OnSiteToDelete siteDeleter;

    public SiteListAdapter(Context c, ArrayList<Site> s, OnSiteToDelete sD) {
        context = c;
        siteList = s;
        siteDeleter = sD;
    }

    @Override
    public int getCount() {
        return siteList.size();
    }

    @Override
    public Site getItem(int i) {
        return siteList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return siteList.get(i).getId();
    }

    // Returns the view of an item of the list
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.sitelist_item, null);
        }

        TextView name = (TextView) view.findViewById(R.id.sitelist_item_name);
        name.setText(getItem(position).getNom());

        TextView adress = (TextView) view.findViewById(R.id.sitelist_item_adress);
        adress.setText(getItem(position).getAdresse());

        TextView category = (TextView) view.findViewById(R.id.sitelist_item_category);
        category.setText(getItem(position).getCategorie());

        TextView summary = (TextView) view.findViewById(R.id.sitelist_item_summary);
        summary.setText(getItem(position).getResume());

        GridLayout grid = (GridLayout) view.findViewById(R.id.gridSiteList);
        grid.getWidth();

        // Deletion button : will delete a site in the local list and calls the object which will delete the site in the db
        Button delButton = (Button) view.findViewById(R.id.sitelist_item_delButton);
        delButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                siteDeleter.deleteSite(getItem(position));
                siteList.remove(getItem(position));
                notifyDataSetChanged();
            }
        });
        return view;
    }
}
