package com.tradehero.th.api.analytics;

public class SearchSecurityEventForm implements AnalyticsDTO
{
    public String eventType;
    public String timestampUtc;
    public Integer securityId;
    public Integer userId;

    public SearchSecurityEventForm(String eventType, String timestampUtc, Integer securityId,Integer userId)
    {
        this.eventType = eventType;
        this.timestampUtc = timestampUtc;
        this.securityId = securityId;
        this.userId = userId;
    }
}
