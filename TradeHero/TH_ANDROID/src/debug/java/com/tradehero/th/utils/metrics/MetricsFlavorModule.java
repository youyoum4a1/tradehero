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
public class MetricsFlavorModule
{
    @Provides public THAppsFlyer provideTHAppsFlyer(Context context)
    {
        return new THAppsFlyer()
        {
            @Override public void setAppsFlyerKey(@NonNull String key)
            {
                // Nothing to do in debug mode
            }

            @Override public void sendTracking(@NonNull Context applicationContext)
            {
                // Nothing to do in debug mode
            }
        };
    }
}
