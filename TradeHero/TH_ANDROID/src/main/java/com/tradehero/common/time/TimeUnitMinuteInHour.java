package com.tradehero.common.time;

/**
 * Created by xavier on 1/23/14.
 */
public class TimeUnitMinuteInHour extends TimeUnitMinute
{
    public static final String TAG = TimeUnitMinuteInHour.class.getSimpleName();
    public static final long MAX_MINUTE_IN_HOUR = 59;

    public TimeUnitMinuteInHour()
    {
    }

    @Override public long getMaxQuantity()
    {
        return MAX_MINUTE_IN_HOUR;
    }
}
