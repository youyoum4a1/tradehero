package com.tradehero.th.utils.metrics.events;

import com.tradehero.metrics.AnalyticsProfileEvent;
import java.util.Collection;

public class ProfileEvent extends AnalyticsProfileEvent
{
    public ProfileEvent(String name, Collection<String> collection)
    {
        super(name, collection);
    }
}
