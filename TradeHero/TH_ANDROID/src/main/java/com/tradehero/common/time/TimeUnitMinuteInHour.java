package com.tradehero.common.time;

public class TimeUnitMinuteInHour extends TimeUnitMinute
{
    public static final long MAX_MINUTE_IN_HOUR = 59;

    public TimeUnitMinuteInHour()
    {
    }

    @Override public long getMaxQuantity()
    {
        return MAX_MINUTE_IN_HOUR;
    }
}
