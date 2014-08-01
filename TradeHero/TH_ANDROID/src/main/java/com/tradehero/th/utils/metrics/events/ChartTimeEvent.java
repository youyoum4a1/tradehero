package com.tradehero.th.utils.metrics.events;

import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.models.chart.ChartTimeSpan;
import com.tradehero.th.models.chart.ChartTimeSpanMetricsCodeFactory;
import com.tradehero.th.utils.metrics.AnalyticsConstants;

public class ChartTimeEvent extends SecurityEvent
{
    public ChartTimeEvent(SecurityId securityId, ChartTimeSpan chartTimeSpan)
    {
        super(String.format(AnalyticsConstants.PickChart, ChartTimeSpanMetricsCodeFactory.createCode(chartTimeSpan)), securityId);
    }
}
