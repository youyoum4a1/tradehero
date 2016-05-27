package com.tradehero.th.activities;

import android.content.Context;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.utils.Constants;

import java.util.Date;

import io.fabric.sdk.android.Fabric;

public class ActivityBuildTypeUtil
{
    public static void startCrashReports(@NonNull Context context)
    {
        Fabric.with(context, new Crashlytics());
        //Crashlytics.start(context);
        Crashlytics.setString(Constants.TH_CLIENT_TYPE, String.format("%s:%s", Constants.DEVICE_TYPE, Constants.TAP_STREAM_TYPE.name()));
    }

    public static void setUpCrashReports(@NonNull UserBaseKey currentUserKey)
    {
        Crashlytics.setUserIdentifier("" + currentUserKey.key);
    }

    public static void flagLowMemory()
    {
        Crashlytics.setString("LowMemoryAt", new Date().toString());
    }
}
