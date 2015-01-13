package com.tradehero.th.utils.metrics.appsflyer;

import android.content.Context;
import android.support.annotation.NonNull;

public interface THAppsFlyer
{
    void setAppsFlyerKey(@NonNull String key);
    void sendTracking(@NonNull Context applicationContext);
}
