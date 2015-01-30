package com.tradehero.th.utils.metrics.events;

import com.tradehero.metrics.AnalyticsEvent;
import java.util.Map;

public abstract class THAnalyticsEvent extends AnalyticsEvent
{
    //<editor-fold desc="Constructors">
    public THAnalyticsEvent(String name)
    {
        super(name);
    }

    public THAnalyticsEvent(String name, Map<String, String> attributes)
    {
        super(name, attributes);
    }
    //</editor-fold>
}