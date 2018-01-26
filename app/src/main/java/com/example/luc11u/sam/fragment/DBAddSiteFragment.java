package com.example.luc11u.sam.fragment;

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

import com.example.luc11u.sam.R;
import com.example.luc11u.sam.listener.AddTextFieldsWatcher;
import com.example.luc11u.sam.parentInterface.OnFieldsToCheck;
import com.example.luc11u.sam.parentInterface.OnSiteToAdd;

// Fragment displaying the "add a new site" section of the app
public class DBAddSiteFragment extends Fragment implements OnFieldsToCheck {

    private Button addButton;
    private EditText editName, editAdress, editSummary;
    private Spinner categoryChooser;
    // Object which will register the addition of a new site
    private OnSiteToAdd clickDataReceiver;

    public DBAddSiteFragment() {
        // Required empty public constructor
    }

    // Checks if the parent can register the addition of a new site
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

        // Puts items in the category sinner
        categoryChooser = (Spinner) view.findViewById(R.id.choiceCategory);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(), R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryChooser.setAdapter(adapter);

        // Attach listeners to the editTexts for name and adress fields
        editName = (EditText) view.findViewById(R.id.edit_newSiteName);
        editName.addTextChangedListener(new AddTextFieldsWatcher(this));
        editAdress = (EditText) view.findViewById(R.id.edit_newSiteAdress);
        editAdress.addTextChangedListener(new AddTextFieldsWatcher(this));

        editSummary = (EditText) view.findViewById(R.id.edit_newSiteSummary);

        // Attachs a listener to the "Add" button
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

    // Checks if the address and name fields are not empty and enables the add button accordingly
    @Override
    public void checkRequiredFields(){
        if (editName.getText().toString().isEmpty() || editAdress.getText().toString().isEmpty()) {
            addButton.setEnabled(false);
        } else {
            addButton.setEnabled(true);
        }
    }


}
