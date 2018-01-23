package com.example.luc11u.sam;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


public class DBAddSite extends Fragment {

    Button addButton;
    EditText editName, editAdress, editSummary;
    Spinner categoryChooser;
    OnSiteToAdd clickDataReceiver;

    public DBAddSite() {
        // Required empty public constructor
    }


    private void onAttachToParentFragment(Fragment fragment) {
        try {
            clickDataReceiver = (OnSiteToAdd)fragment;
        } catch (ClassCastException e){
            throw new ClassCastException(
                    "Parent fragment must implement OnPlayerSelectionSetListener : " + e.getLocalizedMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dbadd_site, container, false);


        onAttachToParentFragment(getParentFragment());


        categoryChooser = (Spinner) view.findViewById(R.id.choiceCategory);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(), R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categoryChooser.setAdapter(adapter);




        editName = (EditText) view.findViewById(R.id.edit_newSiteName);
        editName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkRequiredFields();
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editAdress = (EditText) view.findViewById(R.id.edit_newSiteAdress);
        editAdress.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkRequiredFields();
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editSummary = (EditText) view.findViewById(R.id.edit_newSiteSummary);


        addButton = (Button) view.findViewById(R.id.button_addNewSite);
        addButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickDataReceiver.addSite(
                        editName.getText().toString(),
                        categoryChooser.getSelectedItem().toString(),
                        editAdress.getText().toString(),
                        editSummary.getText().toString());
            }
        });

        checkRequiredFields();


        return view;
    }

    private void checkRequiredFields(){
        if (editName.getText().toString().isEmpty() || editAdress.getText().toString().isEmpty()) {
            addButton.setEnabled(false);
        } else {
            addButton.setEnabled(true);
        }
    }


}
