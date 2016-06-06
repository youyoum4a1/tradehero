package com.androidth.general.models.push.urbanairship;

import android.app.Activity;
import android.support.annotation.NonNull;
import com.urbanairship.google.PlayServicesUtils;

public class UrbanAirshipTools
{
    public static void verify(@NonNull Activity activity)
    {
        // Handle any Google Play Services errors
        if (PlayServicesUtils.isGooglePlayStoreAvailable())
        {
            PlayServicesUtils.handleAnyPlayServicesError(activity);
        }
    }
}
