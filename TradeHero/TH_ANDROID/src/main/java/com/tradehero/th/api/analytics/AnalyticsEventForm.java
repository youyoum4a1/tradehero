package com.tradehero.th.api.analytics;

import java.util.Date;

/** Created with IntelliJ IDEA. User: xavier Date: 11/14/13 Time: 7:38 PM To change this template use File | Settings | File Templates. */
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
