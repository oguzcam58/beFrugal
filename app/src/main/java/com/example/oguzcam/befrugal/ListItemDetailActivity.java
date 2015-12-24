package com.example.oguzcam.befrugal;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.oguzcam.befrugal.model.ListContract;

public class ListItemDetailActivity extends AppCompatActivity {

    private final String LIST_ITEM_DETAIL_FRAGMENT_TAG = "LIDF_TAG";
    private Long mListItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item_detail_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(savedInstanceState == null) {
            ListItemDetailActivityFragment fragment = new ListItemDetailActivityFragment();

            Bundle arguments = new Bundle();
            Uri uri = getIntent().getData();
            if(uri != null) {
                mListItemId = ListContract.ListItemEntry.getListIdFromUri(uri);
                arguments.putParcelable(ListItemDetailActivityFragment.LIST_ITEM_DETAIL_URI, uri);
                fragment.setArguments(arguments);
            }

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.list_item_detail_container, fragment, LIST_ITEM_DETAIL_FRAGMENT_TAG)
                    .commit();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
