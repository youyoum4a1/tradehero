package com.tradehero.th.utils.metrics.events;

import com.tradehero.th.utils.metrics.AnalyticsConstants;

public class AppLaunchEvent extends AnalyticsEvent
{
    public AppLaunchEvent()
    {
        super(AnalyticsConstants.AppLaunch);
    }
}
