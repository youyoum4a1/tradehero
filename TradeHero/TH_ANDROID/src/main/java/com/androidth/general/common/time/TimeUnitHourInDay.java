package com.androidth.general.common.time;

abstract public class TimeUnitHourInDay extends TimeUnitHour
{
    public static final long MAX_HOUR_IN_DAY = 23;

    public TimeUnitHourInDay()
    {
    }

    @Override public long getMaxQuantity()
    {
        return MAX_HOUR_IN_DAY;
    }
}
