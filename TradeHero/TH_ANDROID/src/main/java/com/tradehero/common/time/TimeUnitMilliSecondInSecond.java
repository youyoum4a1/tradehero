package com.tradehero.common.time;

public class TimeUnitMilliSecondInSecond extends TimeUnitMilliSecond
{
    public static final long MAX_MILLISECOND_IN_MILLISEC = 999;

    public TimeUnitMilliSecondInSecond()
    {
    }

    @Override public long getMaxQuantity()
    {
        return MAX_MILLISECOND_IN_MILLISEC;
    }
}
