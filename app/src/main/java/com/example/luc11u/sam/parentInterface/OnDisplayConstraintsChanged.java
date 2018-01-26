package com.example.luc11u.sam.parentInterface;

// Used to update the constrints of the sites displayed (category and distance to current location)
public interface OnDisplayConstraintsChanged {
    public void updateCategoryConstraint(String c);

    public void updateRangeConstraint(int r);
}
