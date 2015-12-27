package com.oguzcam.befrugal;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.oguzcam.befrugal.R;
import com.oguzcam.befrugal.model.ListContract;

import java.util.Date;

/**
 * Created by oguzcam on 05/09/15.
 */
public class ShoppingListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ShoppingListFragment.class.getSimpleName();

    private static final String[] LIST_COLUMNS = {
            ListContract.ListEntry._ID,
            ListContract.ListEntry.COLUMN_LIST_NAME
    };

    // These indices are tied to LIST_COLUMNS. If LIST_COLUMNS changes, these
    // must change.
    static final int COL_LIST_ID = 0;
    static final int COL_LIST_NAME = 1;

    private static final int LIST_LOADER = 0;
    private ShoppingListAdapter mListAdapter;

    public ShoppingListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mListAdapter = new ShoppingListAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.shopping_list_fragment, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.list_view_shopping_list);
        listView.setAdapter(mListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    Long listId = cursor.getLong(COL_LIST_ID);
                    Intent intent = new Intent(getActivity(), ListItemActivity.class)
                            .setData(ListContract.ListItemEntry.buildListItemWithListId(listId));
                    startActivity(intent);
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    showListOptionsDialog(cursor);
                }
                return true;
            }
        });

        return rootView;
    }

    // Show delete update options
    private void showListOptionsDialog(final Cursor cursor) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        // set title
        alertDialogBuilder.setTitle(cursor.getString(COL_LIST_NAME));
        // set dialog content
        alertDialogBuilder
                .setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, new String[]{"Delete List", "Update List Name"}),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        showDeleteConfirmationDialog(cursor);
                                        break;
                                    case 1:
                                        showUpdateListDialog(cursor);
                                        break;
                                    default:
                                        Log.v(TAG, "Default case called!" + cursor.getString(COL_LIST_NAME));
                                        break;
                                }
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog(final Cursor cursor) {
        final String listName = cursor.getString(COL_LIST_NAME);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        // set dialog content
        alertDialogBuilder
                .setMessage(getString(R.string.delete_confirmation, listName))
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        getActivity().getContentResolver().delete(
                                ListContract.ListEntry.CONTENT_URI,
                                ListContract.ListEntry._ID + "=?",
                                new String[]{(Long.toString(cursor.getLong(COL_LIST_ID)))});
                        Snackbar.make(getView(),
                                getString(R.string.delete_snackbar, listName),
                                Snackbar.LENGTH_LONG)
                                .show();
                    }
                })
                .setCancelable(false)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void showUpdateListDialog(final Cursor cursor) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        final String listName = cursor.getString(COL_LIST_NAME);
        final String listId = cursor.getString(COL_LIST_ID);

        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_new_list, null);
        final EditText editText = (EditText) dialogView.findViewById(R.id.new_list_name);
        editText.setText(listName);

        // set title
        alertDialogBuilder.setTitle(getString(R.string.update) + " " + listName);
        // set dialog content
        alertDialogBuilder
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String newListName = editText.getText().toString();
                        ContentValues values = new ContentValues();
                        values.put(ListContract.ListEntry.COLUMN_CREATION_DATE, new Date().getTime());
                        values.put(ListContract.ListEntry.COLUMN_LIST_NAME, newListName);

                        getActivity().getContentResolver().update(
                                ListContract.ListEntry.CONTENT_URI,
                                values,
                                ListContract.ListEntry._ID + "=?",
                                new String[]{listId});

                        // inform user
                        Snackbar.make(getView(),
                                getString(R.string.update_snackbar, listName, newListName),
                                Snackbar.LENGTH_LONG)
                                .show();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
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
        if ( null != weatherForLocationUri ) {
            return new CursorLoader(getActivity(),
                    weatherForLocationUri,
                    LIST_COLUMNS,
                    null,
                    null,
                    sortOrder
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            mListAdapter.swapCursor(data);
        } else {
            mListAdapter.swapCursor(null);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mListAdapter.swapCursor(null);
    }
}
