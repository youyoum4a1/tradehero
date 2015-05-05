package com.tradehero.th.utils.metrics.events;

import com.tradehero.metrics.AnalyticsEvent;
import java.util.Collections;

public class SingleAttributeEvent extends AnalyticsEvent
{
    public SingleAttributeEvent(String name, String attributeKey, String attributeValue)
    {
        super(name, Collections.singletonMap(attributeKey, attributeValue));
    }
}
