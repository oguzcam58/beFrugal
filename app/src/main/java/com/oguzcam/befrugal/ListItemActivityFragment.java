package com.oguzcam.befrugal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.oguzcam.befrugal.model.ListContract;

import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class ListItemActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ListItemActivityFragment.class.getSimpleName();

    private Uri mUri;
    static final String LIST_DETAIL_URI = "LD_URI";

    private static final int LIST_ITEM_LOADER = 0;
    private ListItemListAdapter mListAdapter;

    private static final String[] LIST_COLUMNS = {
            ListContract.ListItemEntry.TABLE_NAME + "." + ListContract.ListItemEntry._ID,
            ListContract.ListItemEntry.COLUMN_LIST_ITEM_NAME,
            ListContract.ListItemEntry.COLUMN_UNIT_AMOUNT,
            ListContract.ListItemEntry.COLUMN_TOTAL_AMOUNT,
            ListContract.ListItemEntry.COLUMN_DONE,
            ListContract.ListItemEntry.COLUMN_LIST_ID
    };

    public static final int COL_LIST_ITEM_ID = 0;
    public static final int COL_LIST_ITEM_NAME = 1;
    public static final int COL_UNIT_AMOUNT = 2;
    public static final int COL_TOTAL_AMOUNT = 3;
    public static final int COL_DONE = 4;
    public static final int COL_LIST_ID = 5;

    private TextToSpeech toSpeech;

    private String toPlay = new String();

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
        }

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toSpeech = new TextToSpeech(getContext().getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    toSpeech.setLanguage(Locale.getDefault());
                }
            }
        });

        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        // Inflate menu resource file.
        inflater.inflate(R.menu.menu_list_items, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem menuItem = menu.findItem(R.id.action_play);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (toSpeech != null && toPlay != null) {
                    toSpeech.speak(toPlay, TextToSpeech.QUEUE_FLUSH, null, "toPlay");
                }
                return true;
            }
        });
    }


    // Show delete update options
    private void showListOptionsDialog(final Cursor cursor) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        // set title
        alertDialogBuilder.setTitle(cursor.getString(COL_LIST_ITEM_NAME));
        // set dialog content
        alertDialogBuilder
                .setAdapter(new ArrayAdapter<String>(
                                getActivity(),
                                android.R.layout.select_dialog_item,
                                new String[]{getString(R.string.delete_item)}),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        showDeleteConfirmationDialog(cursor);
                                        break;
                                    default:
                                        Log.v(TAG, "Default case called!" + cursor.getString(COL_LIST_ITEM_NAME));
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
        final String listName = cursor.getString(COL_LIST_ITEM_NAME);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        // set dialog content
        alertDialogBuilder
                .setMessage(getString(R.string.delete_confirmation, listName))
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        long listItemId = cursor.getLong(COL_LIST_ITEM_ID);
                        deleteTotalAmount(cursor);

                        getActivity().getContentResolver().delete(
                                ListContract.ListItemEntry.CONTENT_URI,
                                ListContract.ListItemEntry._ID + "=?",
                                new String[]{(Long.toString(listItemId))});
                        Snackbar.make(getView(),
                                getString(R.string.delete_snackbar, listName),
                                Snackbar.LENGTH_LONG)
                                .show();

                        restartLoader();
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

    private void deleteTotalAmount(final Cursor cursor) {
        long listId = cursor.getLong(COL_LIST_ID);
        double totalAmount = cursor.getDouble(COL_TOTAL_AMOUNT);
        int done = cursor.getInt(COL_DONE);

        if (done == 1 && totalAmount > 0) {
            Utility.addToListTotalAmount(getContext(), listId, totalAmount * -1);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        restartLoader();
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(LIST_ITEM_LOADER, null, this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LIST_ITEM_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            StringBuilder sortOrder = new StringBuilder();
            sortOrder
                    .append(ListContract.ListItemEntry.COLUMN_DONE + " ASC, ")
                    .append(ListContract.ListItemEntry.COLUMN_LAST_UPDATED_TIME + " DESC ");

            return new CursorLoader(getActivity(),
                    mUri,
                    LIST_COLUMNS,
                    null,
                    null,
                    sortOrder.toString()
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            mListAdapter.swapCursor(data);
            toPlay = Utility.setItemNames(data, COL_LIST_ITEM_NAME);
        } else {
           mListAdapter.swapCursor(null);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mListAdapter.swapCursor(null);
    }
}
