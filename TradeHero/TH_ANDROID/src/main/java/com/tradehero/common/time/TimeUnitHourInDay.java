package com.tradehero.common.time;

import org.ocpsoft.prettytime.TimeUnit;

/**
 * Created by xavier on 1/23/14.
 */
public class TimeUnitHourInDay implements TimeUnit
{
    public static final String TAG = TimeUnitHourInDay.class.getSimpleName();
    public static final long MILLIS_PER_HOUR = 3600000;
    public static final long MAX_HOUR_IN_DAY = 23;

    public TimeUnitHourInDay()
    {
    }

    @Override public long getMillisPerUnit()
    {
        return MILLIS_PER_HOUR;
    }

    @Override public long getMaxQuantity()
    {
        return MAX_HOUR_IN_DAY;
    }
}
