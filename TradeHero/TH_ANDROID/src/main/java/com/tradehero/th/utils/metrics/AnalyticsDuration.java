package com.tradehero.th.utils.metrics;

import android.support.annotation.NonNull;

public class AnalyticsDuration
{
    private final long durationSec;

    protected AnalyticsDuration(long durationSec)
    {
        this.durationSec = durationSec;
    }

    @NonNull public static AnalyticsDuration sinceTimeMillis(long beginTime)
    {
        return fromDurationMilliSec(System.currentTimeMillis() - beginTime);
    }

    @NonNull public static AnalyticsDuration fromDurationMilliSec(long durationMilliSec)
    {
        return fromDurationSec(durationMilliSec / 1000);
    }

    @NonNull public static AnalyticsDuration fromDurationSec(long durationSec)
    {
        return new AnalyticsDuration(durationSec);
    }

    @Override @NonNull public String toString()
    {
        String s;
        if (durationSec <= AnalyticsConstants.MaxTime10Sec)
        {
            s = AnalyticsConstants.Time1T10S;
        }
        else if (durationSec <= AnalyticsConstants.MaxTime30Sec)
        {
            s = AnalyticsConstants.Time11T30S;
        }
        else if (durationSec <= AnalyticsConstants.MaxTime60Sec)
        {
            s = AnalyticsConstants.Time31T60S;
        }
        else if (durationSec <= AnalyticsConstants.MaxTime3Min)
        {
            s = AnalyticsConstants.Time1T3M;
        }
        else if (durationSec <= AnalyticsConstants.MaxTime10Min)
        {
            s = AnalyticsConstants.Time3T10M;
        }
        else
        {
            s = AnalyticsConstants.Time10M;
        }
        return s;
    }
}
