package com.tradehero.th.utils.metrics.events;

import java.util.Map;

public class AttributesEvent extends THAnalyticsEvent
{
    //<editor-fold desc="Constructors">
    public AttributesEvent(String name, Map<String, String> attributes)
    {
        super(name, attributes);
    }
    //</editor-fold>
}
