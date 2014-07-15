package com.tradehero.th.utils.metrics.events;

import java.util.HashMap;
import java.util.Map;

public abstract class AnalyticsEvent
{
    private final String name;
    private final Map<String, String> attributes = new HashMap<>();

    public AnalyticsEvent(String name)
    {
        this.name = name;
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
