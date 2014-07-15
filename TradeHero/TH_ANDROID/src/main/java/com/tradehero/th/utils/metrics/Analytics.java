package com.tradehero.th.utils.metrics;

import com.squareup.otto.Bus;
import com.tradehero.th.utils.metrics.events.AnalyticsEvent;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Analytics
{
    private final Bus bus;
    private Set<AnalyticsEvent> pendingEvents = new LinkedHashSet<>();

    @Inject public Analytics(Bus bus)
    {
        this.bus = bus;
    }

    public final Analytics addEvent(AnalyticsEvent analyticsEvent)
    {
        pendingEvents.add(analyticsEvent);
        return this;
    }

    public final void fireEvent(AnalyticsEvent analyticsEvent)
    {
        // TODO
    }
}
