package com.oguzcam.befrugal;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.oguzcam.befrugal.model.ListContract;

import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String SHOPPINGLISTFRAGMENT_TAG = "SHOPLISTTAG";
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //setTitle("Welcome");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ShoppingListFragment(), SHOPPINGLISTFRAGMENT_TAG)
                    .commit();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show dialog to get user input as list name
                showNewListDialog();
            }
        });


    }

    // Get list name input from user
    private void showNewListDialog() {
        final View dialogView = MainActivity.this.getLayoutInflater().inflate(R.layout.dialog_new_list, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        // set dialog message
        alertDialogBuilder
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton(R.string.add, null)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button btn = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                btn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        final String listName = ((EditText) dialogView.findViewById(R.id.new_list_name)).getText().toString();

                        // Create new list entry if list name is provided
                        if (listName.trim().isEmpty()) {
                            Snackbar.make(dialogView,
                                    getString(R.string.error_list_name_empty),
                                    Snackbar.LENGTH_LONG)
                                    .show();
                        } else {
                            ContentValues values = new ContentValues();
                            long time = new Date().getTime();
                            values.put(ListContract.ListEntry.COLUMN_CREATION_TIME, time);
                            values.put(ListContract.ListEntry.COLUMN_LAST_UPDATED_TIME, time);
                            values.put(ListContract.ListEntry.COLUMN_LIST_NAME, listName);

                            getContentResolver().insert(ListContract.ListEntry.CONTENT_URI, values);

                            Snackbar.make(findViewById(android.R.id.content),
                                    getString(R.string.add_snackbar, listName),
                                    Snackbar.LENGTH_LONG)
                                    .show();
                            //Dismiss once everything is OK.
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        });

        // show it
        alertDialog.show();
    }

}
