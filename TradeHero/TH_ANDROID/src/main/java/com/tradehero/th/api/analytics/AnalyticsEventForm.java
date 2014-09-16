package com.tradehero.th.api.analytics;

public class AnalyticsEventForm implements AnalyticsDTO
{
    public String eventType;
    public String timestampUtc;
    public Integer advertisementId;
    public Integer providerId;
    public String pageName;
    public Integer userId;

    public AnalyticsEventForm(String eventType, String timestampUtc, Integer advertisementId,
            Integer providerId, Integer userId)
    {
        this.eventType = eventType;
        this.timestampUtc = timestampUtc;
        this.advertisementId = advertisementId;
        this.providerId = providerId;
        this.userId = userId;
    }
}
