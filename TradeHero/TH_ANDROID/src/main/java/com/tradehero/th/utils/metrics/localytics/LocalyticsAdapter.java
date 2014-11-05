package com.tradehero.th.utils.metrics.localytics;

import android.content.Context;
import com.localytics.android.LocalyticsSession;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.metrics.AnalyticsAdapter;
import com.tradehero.th.utils.metrics.events.AnalyticsEvent;
import java.util.ArrayList;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;

@Singleton
public class LocalyticsAdapter
        implements AnalyticsAdapter
{
    private final LocalyticsSession localytics;

    @Inject LocalyticsAdapter(
            @NonNull Context context,
            @NonNull @ForLocalytics String appKey)
    {
        localytics = new LocalyticsSession(context, appKey);
        LocalyticsSession.setLoggingEnabled(Constants.RELEASE);
    }

    @Override public void open(Set<String> customDimensions)
    {
        localytics.open(new ArrayList<>(customDimensions));
    }

    @Override public void addEvent(AnalyticsEvent analyticsEvent)
    {
        localytics.tagEvent(analyticsEvent.getName(), analyticsEvent.getAttributes());
    }

    @Override public void tagScreen(String screenName)
    {
        localytics.tagScreen(screenName);
    }

    @Override public void close(Set<String> customDimensions)
    {
        localytics.close(new ArrayList<>(customDimensions));
        localytics.upload();
    }
}
