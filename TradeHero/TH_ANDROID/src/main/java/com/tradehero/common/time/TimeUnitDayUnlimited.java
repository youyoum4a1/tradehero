package com.tradehero.common.time;

public class TimeUnitDayUnlimited extends TimeUnitDay
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
