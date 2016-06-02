package com.tradehero.th.models.push.urbanairship;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.urbanairship.google.PlayServicesUtils;

public class UrbanAirshipTools
{
    public static void verify(@NonNull Activity activity)
    {
        // Handle any Google Play Services errors
        //isGooglePlayServicesAvailable
        if (PlayServicesUtils.isGooglePlayServicesAvailable(activity.getApplicationContext()) == ConnectionResult.SUCCESS)
        {
            PlayServicesUtils.handleAnyPlayServicesError(activity);
        }
    }
}
