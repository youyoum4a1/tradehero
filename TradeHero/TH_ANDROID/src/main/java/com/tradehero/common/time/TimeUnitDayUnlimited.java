package com.tradehero.common.time;


public class TimeUnitDayUnlimited extends TimeUnitDay
{
    public static final String TAG = TimeUnitDayUnlimited.class.getSimpleName();

    public TimeUnitDayUnlimited()
    {
        super();
    }

    @Override public long getMaxQuantity()
    {
        return Long.MAX_VALUE;
    }
}
