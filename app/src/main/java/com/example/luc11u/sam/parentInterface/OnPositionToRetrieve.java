package com.example.luc11u.sam.parentInterface;

import com.google.android.gms.maps.model.LatLng;

// Used to send down the current location to children components
public interface OnPositionToRetrieve {
    public LatLng sendPosition();
}
