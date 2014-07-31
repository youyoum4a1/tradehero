package com.tradehero.th.utils.metrics.events;

import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.utils.metrics.AnalyticsConstants;

public class TrendingStockEvent extends SecurityEvent
{
    public TrendingStockEvent(SecurityId securityId)
    {
        super(AnalyticsConstants.TrendingStock, securityId);
    }
}
