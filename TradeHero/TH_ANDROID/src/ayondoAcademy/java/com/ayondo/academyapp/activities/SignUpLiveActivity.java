package com.ayondo.academyapp.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.androidth.general.activities.OneFragmentActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.androidth.general.fragments.live.LiveSignUpMainFragment;
import timber.log.Timber;

public class SignUpLiveActivity extends OneFragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    private GoogleApiClient mGoogleApiClient;

    @NonNull @Override protected Class<? extends Fragment> getInitialFragment()
    {
        return LiveSignUpMainFragment.class;
    }

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override protected void onStart()
    {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override protected void onStop()
    {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override public void onConnected(Bundle bundle)
    {
        Timber.d("connected to Play Services");
    }

    @Override public void onConnectionSuspended(int i)
    {
        Timber.d("connection suspended to Play Services %d", i);
    }

    @Override public void onConnectionFailed(ConnectionResult connectionResult)
    {
        Timber.d("Failed to connect to Play Services : %s", connectionResult);
    }
}