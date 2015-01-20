package com.tradehero.th.utils.metrics.events;

import java.util.Map;

public class AttributesEvent extends THAnalyticsEvent
{
    public AttributesEvent(String name, Map<String, String> attributes)
    {
        super(name, attributes);
    }
}
