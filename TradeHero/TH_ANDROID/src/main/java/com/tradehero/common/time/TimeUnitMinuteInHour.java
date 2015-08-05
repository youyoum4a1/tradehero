package com.tradehero.common.time;

abstract public class TimeUnitMinuteInHour extends TimeUnitMinute
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
