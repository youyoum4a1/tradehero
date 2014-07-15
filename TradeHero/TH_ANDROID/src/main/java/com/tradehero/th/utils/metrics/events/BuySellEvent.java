package com.tradehero.th.utils.metrics.events;

import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.utils.metrics.AnalyticsConstants;

public class BuySellEvent extends SecurityEvent
{
    public BuySellEvent(boolean isBuyEvent, SecurityId securityId)
    {
        super(isBuyEvent ? AnalyticsConstants.Trade_Buy : AnalyticsConstants.Trade_Sell, securityId);
    }
}
