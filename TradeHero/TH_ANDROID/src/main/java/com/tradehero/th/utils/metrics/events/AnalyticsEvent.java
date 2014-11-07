package com.tradehero.th.utils.metrics.events;

import java.util.Map;

public abstract class AnalyticsEvent extends com.tradehero.metrics.AnalyticsEvent
{
    public AnalyticsEvent(String name)
    {
        super(name);
    }

    public AnalyticsEvent(String name, Map<String, String> attributes)
    {
        super(name, attributes);
    }
}