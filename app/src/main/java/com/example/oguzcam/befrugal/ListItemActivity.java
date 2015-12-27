package com.example.oguzcam.befrugal;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.oguzcam.befrugal.model.ListContract;

import java.util.Date;

public class ListItemActivity extends AppCompatActivity {

    private static final String LIST_ITEM_FRAGMENT_TAG = "LIF_TAG";

    private Long mListId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(savedInstanceState == null) {
            Bundle arguments = new Bundle();
            Uri uri = getIntent().getData();
            mListId = ListContract.ListItemEntry.getListIdFromUri(uri);
            arguments.putParcelable(ListItemActivityFragment.LIST_DETAIL_URI, uri);

            ListItemActivityFragment fragment = new ListItemActivityFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.list_items_container, fragment, LIST_ITEM_FRAGMENT_TAG)
                    .commit();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListItemActivity.this, ListItemDetailActivity.class);
                intent.putExtra(ListItemDetailActivityFragment.LIST_ITEM_DETAIL_LIST_ID, mListId);
                startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
