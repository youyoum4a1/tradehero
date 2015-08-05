package com.tradehero.common.time;

abstract public class TimeUnitSecondInMinute extends TimeUnitSecond
{
    public static final long MAX_SECOND_IN_MINUTE = 59;

    public TimeUnitSecondInMinute()
    {
        super();
    }

    @Override public long getMaxQuantity()
    {
        return MAX_SECOND_IN_MINUTE;
    }
}
