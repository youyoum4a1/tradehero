package com.tradehero.th.utils.metrics.events;

import com.tradehero.th.utils.metrics.AnalyticsConstants;

public class ScreenFlowEvent extends SingleAttributeEvent
{
    public ScreenFlowEvent(String name, String fromScreen)
    {
        super(name, AnalyticsConstants.FollowedFromScreen, fromScreen);
    }
}
