package com.androidth.general.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.androidth.general.R;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.fragments.live.LiveSignUpMainFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.tradehero.route.Routable;

import java.io.IOException;
import java.util.List;

import timber.log.Timber;

public class SignUpLiveActivity extends OneFragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    public static final String KYC_CORRESPONDENT_PROVIDER_ID = "KYC.providerId";
    public static final String KYC_CORRESPONDENT_JOIN_COMPETITION = "KYC.joinCompetition";
    protected String currentCountry;

    private final int PERMISSIONS_REQUEST_LOCATION = 10000;

    private GoogleApiClient mGoogleApiClient;
    private Location lastLocation;

    @NonNull @Override protected Class<? extends Fragment> getInitialFragment()
    {
        return LiveSignUpMainFragment.class;
    }

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .enableAutoManage(this, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(LocationServices.API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        thRouter.inject(this);
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
        if (ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION )
                != PackageManager.PERMISSION_GRANTED ) {

            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)){

                Activity activity = this;
                new AlertDialog.Builder(getApplicationContext())
                        .setMessage("TradeHero wants to access your current country location to auto-fill your application form")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(activity,
                                        new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSIONS_REQUEST_LOCATION);
                            }
                        }).setNegativeButton(android.R.string.cancel, null);

            }else{
                ActivityCompat.requestPermissions(this,
                        new String[] {android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_LOCATION
                );
            }

        }else{
            setupLocation();
        }
    }

    private void setupLocation(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION )
                        != PackageManager.PERMISSION_GRANTED) {
            //to double check, otherwise, FusedLocationApi would complain
            return;

        }else{

            lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(lastLocation != null){
                Geocoder geoLocation = new Geocoder(getApplicationContext());
                try {
                    List<Address> add = geoLocation.getFromLocation(lastLocation.getLatitude(), lastLocation.getLongitude(), 1);
                    if(add.isEmpty()){
                        currentCountry = "MY";//default to Malaysia
                    }else{
                        currentCountry = add.get(0).getCountryCode();
                    }

                    SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(getString(R.string.key_preference_country_code), currentCountry);
                    editor.commit();
                } catch (IOException e) {
                    Log.i("Geoloader Exception", e.getMessage());
                } catch (Exception e){
                    THToast.show("Cannot fetch current location");
                }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSIONS_REQUEST_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    setupLocation();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                setupLocation();
                break;
            default:
                break;
        }
    }
}
