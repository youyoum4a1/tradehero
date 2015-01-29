package com.tradehero.th.utils.metrics;

import android.content.Context;
import android.support.annotation.NonNull;
import com.appsflyer.AppsFlyerLib;
import com.tradehero.th.utils.metrics.appsflyer.THAppsFlyer;
import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        complete = false
)
public class MetricsBuildTypeModule
{
    @Provides public THAppsFlyer provideTHAppsFlyer(@NonNull final Context applicationContext)
    {
        return new THAppsFlyer()
        {
            @Override public void setAppsFlyerKey(@NonNull String key)
            {
                AppsFlyerLib.setAppsFlyerKey(key);
            }

            @Override public void sendTracking()
            {
                AppsFlyerLib.sendTracking(applicationContext);
            }

            @Override public void sendTrackingWithEvent(@NonNull String eventName)
            {
                AppsFlyerLib.sendTrackingWithEvent(applicationContext, eventName, "");
            }

            @Override public void sendTrackingWithEvent(@NonNull String eventName, @NonNull String eventRevenueValue)
            {
                AppsFlyerLib.sendTrackingWithEvent(applicationContext, eventName, eventRevenueValue);
            }
        };
    }
}
