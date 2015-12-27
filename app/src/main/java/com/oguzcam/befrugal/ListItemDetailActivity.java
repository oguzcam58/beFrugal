package com.oguzcam.befrugal;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.oguzcam.befrugal.model.ListContract;

public class ListItemDetailActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = ListItemDetailActivity.class.getSimpleName();

    private final String LIST_ITEM_DETAIL_FRAGMENT_TAG = "LIDF_TAG";
    private Long mListItemId;

    private GoogleApiClient mGoogleApiClient;

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

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        if (res == PackageManager.PERMISSION_GRANTED) {
            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                    .getCurrentPlace(mGoogleApiClient, null);
            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                    String[] places = new String[likelyPlaces.getCount()];
                    int i = 0;
                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                        Log.i(TAG, String.format("Place '%s' '%s' has likelihood: %g",
                                placeLikelihood.getPlace().getName(),
                                placeLikelihood.getPlace().getPlaceTypes().toString(),
                                placeLikelihood.getLikelihood()));
                        places[i++] = placeLikelihood.getPlace().getName().toString();
                    }
                    likelyPlaces.release();
                    ListItemDetailActivityFragment fragment = (ListItemDetailActivityFragment) getSupportFragmentManager().findFragmentByTag(LIST_ITEM_DETAIL_FRAGMENT_TAG);
                    fragment.addToPlaceList(places);
                }
            });
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

}
