package com.example.oguzcam.befrugal;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.oguzcam.befrugal.model.ListContract;

import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class ListItemActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ListItemActivityFragment.class.getSimpleName();

    private Uri mUri;
    private long mListId;
    static final String LIST_DETAIL_URI = "LD_URI";

    private static final int LIST_ITEM_LOADER = 0;
    private ListItemListAdapter mListAdapter;

    private static final String[] LIST_COLUMNS = {
            ListContract.ListItemEntry.TABLE_NAME + "." + ListContract.ListItemEntry._ID,
            ListContract.ListItemEntry.COLUMN_LIST_ITEM_NAME
    };

    private static final int COL_LIST_ITEM_ID = 0;
    private static final int COL_LIST_ITEM_NAME = 1;

    public ListItemActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mListAdapter = new ListItemListAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.list_item_activity_fragment, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.list_view_list_items);
        listView.setAdapter(mListAdapter);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(ListItemActivityFragment.LIST_DETAIL_URI);
            Log.v(TAG, "mUri = " + mUri);
            mListId = ListContract.ListItemEntry.getListIdFromUri(mUri);
            Log.v(TAG, "mListId = " + mListId);
        }

//        ContentValues values = new ContentValues();
//        values.put(ListContract.ListItemEntry.COLUMN_LIST_ID, mListId);
//        values.put(ListContract.ListItemEntry.COLUMN_LIST_ITEM_NAME, "alican");
//        values.put(ListContract.ListItemEntry.COLUMN_CREATION_DATE, new Date().getTime());
//        getActivity().getContentResolver().insert(ListContract.ListItemEntry.CONTENT_URI, values);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                Long listId = cursor.getLong(COL_LIST_ITEM_ID);
                Intent intent = new Intent(getActivity(), ListItemDetailActivity.class)
                        .setData(ListContract.ListItemEntry.buildListItemUri(listId));
                startActivity(intent);
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LIST_ITEM_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            String sortOrder = ListContract.ListItemEntry.COLUMN_CREATION_DATE + " DESC ";

            return new CursorLoader(getActivity(),
                    mUri,
                    LIST_COLUMNS,
                    null,
                    null,
                    sortOrder
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            mListAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mListAdapter.swapCursor(null);
    }
}
