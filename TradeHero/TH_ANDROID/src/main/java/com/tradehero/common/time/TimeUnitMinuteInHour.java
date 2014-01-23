package com.tradehero.common.time;

import org.ocpsoft.prettytime.TimeUnit;

/**
 * Created by xavier on 1/23/14.
 */
public class TimeUnitMinuteInHour implements TimeUnit
{
    public static final String TAG = TimeUnitMinuteInHour.class.getSimpleName();
    public static final long MILLIS_PER_MINUTE = 60000;
    public static final long MAX_MINUTE_IN_HOUR = 59;

    public TimeUnitMinuteInHour()
    {
    }

    @Override public long getMillisPerUnit()
    {
        return MILLIS_PER_MINUTE;
    }

    @Override public long getMaxQuantity()
    {
        return MAX_MINUTE_IN_HOUR;
    }
}
