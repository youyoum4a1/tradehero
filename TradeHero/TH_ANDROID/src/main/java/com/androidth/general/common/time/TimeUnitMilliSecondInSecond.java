package com.androidth.general.common.time;

abstract public class TimeUnitMilliSecondInSecond extends TimeUnitMilliSecond
{
    public static final long MAX_MILLISECOND_IN_SECOND = 999;

    public TimeUnitMilliSecondInSecond()
    {
    }

    @Override public long getMaxQuantity()
    {
        return MAX_MILLISECOND_IN_SECOND;
    }
}
