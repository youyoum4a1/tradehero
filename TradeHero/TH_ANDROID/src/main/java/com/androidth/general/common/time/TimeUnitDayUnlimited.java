package com.androidth.general.common.time;

abstract public class TimeUnitDayUnlimited extends TimeUnitDay
{
    public TimeUnitDayUnlimited()
    {
        super();
    }

    @Override public long getMaxQuantity()
    {
        return Long.MAX_VALUE;
    }
}
