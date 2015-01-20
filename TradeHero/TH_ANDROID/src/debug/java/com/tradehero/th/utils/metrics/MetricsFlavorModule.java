package com.tradehero.th.utils.metrics;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.utils.metrics.appsflyer.THAppsFlyer;
import dagger.Module;
import dagger.Provides;
import java.lang.Override;
import java.lang.String;

@Module(
        library = true,
        complete = false
)
public class MetricsFlavorModule
{
    @Provides public THAppsFlyer provideTHAppsFlyer(@NonNull Context applicationContext)
    {
        return new THAppsFlyer()
        {
            @Override public void setAppsFlyerKey(@NonNull String key)
            {
                THToast.show("setAppsFlyerKey " + key);
            }

            @Override public void sendTracking()
            {
                THToast.show("AppsFlyerKey sendTracking");
            }

            @Override public void sendTrackingWithEvent(@NonNull String eventName)
            {
                THToast.show("AppsFlyerKey sendTrackingWithEvent " + eventName);
            }

            @Override public void sendTrackingWithEvent(@NonNull String eventName, @NonNull String eventRevenueValue)
            {
                THToast.show("AppsFlyerKey sendTrackingWithEvent " + eventName + ", " + eventRevenueValue);
            }
        };
    }
}
