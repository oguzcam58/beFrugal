package com.example.oguzcam.befrugal;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.oguzcam.befrugal.model.ListContract;

import java.util.Date;

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
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        final String listName = ((EditText) dialogView.findViewById(R.id.new_list_name)).getText().toString();
                        ContentValues values = new ContentValues();
                        values.put(ListContract.ListEntry.COLUMN_CREATION_DATE, new Date().getTime());
                        values.put(ListContract.ListEntry.COLUMN_LIST_NAME, listName);

                        getContentResolver().insert(ListContract.ListEntry.CONTENT_URI, values);

                        Snackbar.make(findViewById(android.R.id.content),
                                getString(R.string.add_snackbar, listName),
                                Snackbar.LENGTH_LONG)
                                .show();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
