package com.tradehero.common.time;


public class TimeUnitHourInDay extends TimeUnitHour
{
    public static final String TAG = TimeUnitHourInDay.class.getSimpleName();
    public static final long MAX_HOUR_IN_DAY = 23;

    public TimeUnitHourInDay()
    {
    }

    @Override public long getMaxQuantity()
    {
        return MAX_HOUR_IN_DAY;
    }
}
