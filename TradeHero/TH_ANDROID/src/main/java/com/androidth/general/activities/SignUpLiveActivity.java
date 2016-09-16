package com.androidth.general.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.androidth.general.R;
import com.androidth.general.fragments.kyc.LiveSignUpMainFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

import java.io.IOException;
import java.util.List;

import timber.log.Timber;

public class SignUpLiveActivity extends OneFragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    public static final String KYC_CORRESPONDENT_PROVIDER_ID = "KYC.providerId";
    public static final String KYC_CORRESPONDENT_JOIN_COMPETITION = "KYC.joinCompetition";
    protected String currentCountry;

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
                .addApi(LocationServices.API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @NonNull @Override protected Bundle getInitialBundle() {
        Bundle args = super.getInitialBundle();

        int providerId = getIntent().getIntExtra(SignUpLiveActivity.KYC_CORRESPONDENT_PROVIDER_ID, 0);
        boolean isJoining = getIntent().getBooleanExtra(SignUpLiveActivity.KYC_CORRESPONDENT_JOIN_COMPETITION, false);

        LiveSignUpMainFragment.putProviderId(args, providerId);
        LiveSignUpMainFragment.isToJoinCompetition(isJoining);

        return args;
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
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(lastLocation != null){
            Geocoder geoLocation = new Geocoder(getApplicationContext());
            try {
                List<Address> add = geoLocation.getFromLocation(lastLocation.getLatitude(), lastLocation.getLongitude(), 1);
                currentCountry = add.get(0).getCountryCode();
                SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.key_preference_country_code), currentCountry);
                editor.commit();
            } catch (IOException e) {
                Log.i("Geoloader Exception", e.getMessage());
            }
        }

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
