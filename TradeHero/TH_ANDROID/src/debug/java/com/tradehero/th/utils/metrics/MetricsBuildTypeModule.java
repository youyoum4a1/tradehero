package com.tradehero.th.utils.metrics;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.th.utils.metrics.appsflyer.THAppsFlyer;
import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        complete = false
)
public class MetricsBuildTypeModule
{
    @Provides public THAppsFlyer provideTHAppsFlyer(@NonNull Context applicationContext)
    {
        return new THAppsFlyer()
        {
            @Override public void setAppsFlyerKey(@NonNull String key)
            {
            }

            @Override public void sendTracking()
            {
            }

            @Override public void sendTrackingWithEvent(@NonNull String eventName)
            {
            }

            @Override public void sendTrackingWithEvent(@NonNull String eventName, @NonNull String eventRevenueValue)
            {
            }
        };
    }
}
