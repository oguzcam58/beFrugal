package com.oguzcam.befrugal;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.oguzcam.befrugal.R;
import com.oguzcam.befrugal.model.ListContract;

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
            } else {
                mListItemId = getIntent().getLongExtra(ListItemDetailActivityFragment.LIST_ITEM_DETAIL_LIST_ID, 0);
                if(mListItemId != 0) {
                    arguments.putLong(ListItemDetailActivityFragment.LIST_ITEM_DETAIL_LIST_ID, mListItemId);
                    fragment.setArguments(arguments);
                }
            }

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.list_item_detail_container, fragment, LIST_ITEM_DETAIL_FRAGMENT_TAG)
                    .commit();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
