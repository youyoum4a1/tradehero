package com.tradehero.th.utils.metrics.events;

import java.util.HashMap;
import java.util.Map;

public abstract class AnalyticsEvent
{
    private final String name;
    private final Map<String, String> attributes;

    public AnalyticsEvent(String name)
    {
        this(name, new HashMap<String, String>());
    }

    public AnalyticsEvent(String name, Map<String, String> attributes)
    {
        this.name = name;
        this.attributes = attributes;
    }

    public final String getName()
    {
        return name;
    }

    public Map<String, String> getAttributes()
    {
        return attributes;
    }
}
