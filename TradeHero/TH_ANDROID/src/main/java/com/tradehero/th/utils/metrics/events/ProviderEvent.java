package com.tradehero.th.utils.metrics.events;

import com.tradehero.metrics.AnalyticsEvent;

/** This event is incomplete **/
public abstract class ProviderEvent extends AnalyticsEvent
{
    static final String PROVIDER_ID_MAP_KEY = "providerId";

    public ProviderEvent(String name)
    {
        super(name);
    }
}
