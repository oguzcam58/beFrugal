package com.example.oguzcam.befrugal;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.oguzcam.befrugal.model.ListContract;

/**
 * Created by oguzcam on 05/09/15.
 */
public class ShoppingListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String[] LIST_COLUMNS = {
            ListContract.ListEntry._ID,
            ListContract.ListEntry.COLUMN_LIST_NAME
    };

    // These indices are tied to LIST_COLUMNS.  If LIST_COLUMNS changes, these
    // must change.
    static final int COL_LIST_ID = 0;
    static final int COL_LIST_NAME = 1;

    private static final int LIST_LOADER = 0;
    private ShoppingListAdapter mListAdapter;

    public ShoppingListFragment() {
    }

    // Menu Settings Override
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setHasOptionsMenu(true);
//    }
//
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//
//        inflater.inflate(R.menu.forecastfragment, menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if(id == R.id.action_refresh) {
//            updateWeather();
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mListAdapter = new ShoppingListAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.content_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_shoppinglist);
        listView.setAdapter(mListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    Intent intent = new Intent(getActivity(), ListItemsActivity.class)
                            .setData(ListContract.ListItemEntry.buildListItemWithListId(
                                    cursor.getLong(COL_LIST_ID)
                            ));
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LIST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = ListContract.ListEntry.COLUMN_CREATION_DATE + " DESC ";
        Uri weatherForLocationUri = ListContract.ListEntry.buildListUri();

        return new CursorLoader(getActivity(),
                weatherForLocationUri,
                LIST_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mListAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mListAdapter.swapCursor(null);
    }
}
