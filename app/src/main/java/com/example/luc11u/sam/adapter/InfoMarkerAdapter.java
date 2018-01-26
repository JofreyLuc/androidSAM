package com.example.luc11u.sam.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;


// Used to display details of a site on a marker (on multiple lines)
public class InfoMarkerAdapter implements GoogleMap.InfoWindowAdapter {

    // Parent activity
    private Context parent;

    public InfoMarkerAdapter(Context c) {
        parent = c;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    // Returns the view of the marker info
    @Override
    public View getInfoContents(Marker marker) {
        LinearLayout info = new LinearLayout(parent);
        info.setOrientation(LinearLayout.VERTICAL);

        TextView title = new TextView(parent);
        title.setTextColor(Color.BLACK);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(null, Typeface.BOLD);
        title.setText(marker.getTitle());

        TextView snippet = new TextView(parent);
        snippet.setTextColor(Color.GRAY);
        snippet.setText(marker.getSnippet());

        info.addView(title);
        info.addView(snippet);

        return info;
    }
}
