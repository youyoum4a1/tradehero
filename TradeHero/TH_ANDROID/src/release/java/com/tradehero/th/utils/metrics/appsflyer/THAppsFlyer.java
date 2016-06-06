package com.androidth.general.utils.metrics.appsflyer;

import android.content.Context;
import android.support.annotation.NonNull;
import com.appsflyer.AppsFlyerLib;

public class THAppsFlyer
{
    public static void setAppsFlyerKey(@NonNull Context applicationContext, @NonNull String key)
    {
        AppsFlyerLib.setAppsFlyerKey(key);
    }

    public static void sendTracking(@NonNull Context applicationContext)
    {
        AppsFlyerLib.sendTracking(applicationContext);
    }

    public static void sendTrackingWithEvent(@NonNull Context applicationContext, @NonNull String eventName)
    {
        AppsFlyerLib.sendTrackingWithEvent(applicationContext, eventName, "");
    }

    public static void sendTrackingWithEvent(@NonNull Context applicationContext, @NonNull String eventName, @NonNull String eventRevenueValue)
    {
        AppsFlyerLib.sendTrackingWithEvent(applicationContext, eventName, eventRevenueValue);
    }
}
