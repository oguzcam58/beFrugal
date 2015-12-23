package com.example.oguzcam.befrugal;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.oguzcam.befrugal.enums.UnitType;

/**
 * A placeholder fragment containing a simple view.
 */
public class ListItemDetailActivityFragment extends Fragment {

    static final String LIST_ITEM_DETAIL_URI = "LID_URI";

    public ListItemDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_item_detail_fragment, container, false);
        Spinner unitTypeSpinner = (Spinner) rootView.findViewById(R.id.unitType);

        ArrayAdapter<UnitType> unitTypeAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                UnitType.values());

        unitTypeSpinner.setAdapter(unitTypeAdapter);

        return rootView;
    }
}
