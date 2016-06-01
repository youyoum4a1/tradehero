package com.ayondo.academy.models.push.urbanairship;

import android.app.Activity;
import android.support.annotation.NonNull;
import com.urbanairship.amazon.ADMUtils;

public class UrbanAirshipTools
{
    public static void verify(@SuppressWarnings("UnusedParameters") @NonNull Activity activity)
    {
        if (ADMUtils.isADMAvailable())
        {
            ADMUtils.validateManifest();
        }
    }
}
