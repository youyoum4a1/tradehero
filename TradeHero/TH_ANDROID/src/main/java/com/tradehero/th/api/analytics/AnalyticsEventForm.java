package com.tradehero.th.api.analytics;

import java.util.Date;


public class AnalyticsEventForm
{
    public static final String TAG = AnalyticsEventForm.class.getSimpleName();

    public String eventType;
    public Date timestampUtc;
    public Integer advertisementId;
    public Integer providerId;
    public String pageName;
    public int userId;
}
