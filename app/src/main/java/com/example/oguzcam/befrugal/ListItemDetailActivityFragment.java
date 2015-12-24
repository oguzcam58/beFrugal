package com.example.oguzcam.befrugal;

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.oguzcam.befrugal.enums.UnitType;
import com.example.oguzcam.befrugal.model.ListContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class ListItemDetailActivityFragment extends Fragment {

    private static final String TAG = ListItemActivityFragment.class.getSimpleName();

    static final String LIST_ITEM_DETAIL_URI = "LID_URI";

    private Uri mUri;
    private long mListId;

    private EditText mListItemName;

    public ListItemDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_item_detail_fragment, container, false);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(LIST_ITEM_DETAIL_URI);
            Log.v(TAG, "mUri = " + mUri);
            mListId = ListContract.ListItemEntry.getListIdFromUri(mUri);
            Log.v(TAG, "mListId = " + mListId);

            mListItemName = (EditText) rootView.findViewById(R.id.list_item_name);
            Cursor cursor = getActivity().getContentResolver().query(
                    mUri,
                    null,
                    null,
                    null,
                    null
            );

            if(cursor != null && cursor.moveToFirst()){
                int index = cursor.getColumnIndex(ListContract.ListItemEntry.COLUMN_LIST_ITEM_NAME);
                mListItemName.setText(cursor.getString(index));

                cursor.close();
            }
        }

        Spinner unitTypeSpinner = (Spinner) rootView.findViewById(R.id.unit_type);

        ArrayAdapter<UnitType> unitTypeAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                UnitType.values());

        unitTypeSpinner.setAdapter(unitTypeAdapter);

        return rootView;
    }
}
