package com.tradehero.th.utils.metrics.appsflyer;

import android.support.annotation.NonNull;

public interface THAppsFlyer
{
    void setAppsFlyerKey(@NonNull String key);
    void sendTracking();
    void sendTrackingWithEvent(@NonNull String eventName);
    void sendTrackingWithEvent(@NonNull String eventName, @NonNull String eventRevenueValue);
}
