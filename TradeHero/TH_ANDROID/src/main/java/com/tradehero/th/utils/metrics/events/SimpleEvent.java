package com.tradehero.th.utils.metrics.events;

/** Event that identify itself by only event name **/
public final class SimpleEvent extends THAnalyticsEvent
{
    public SimpleEvent(String name)
    {
        super(name);
    }
}
