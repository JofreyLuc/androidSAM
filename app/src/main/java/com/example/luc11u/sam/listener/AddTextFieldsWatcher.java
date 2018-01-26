package com.example.luc11u.sam.listener;

import android.text.Editable;
import android.text.TextWatcher;

import com.example.luc11u.sam.parentInterface.OnFieldsToCheck;

// Listens for changes in the "add a site" section textfields
public class AddTextFieldsWatcher implements TextWatcher {

    private OnFieldsToCheck parentChecker;

    public AddTextFieldsWatcher(OnFieldsToCheck p) {
        parentChecker = p;
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        parentChecker.checkRequiredFields();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
