package com.tradehero.common.time;


public class TimeUnitSecondInMinute extends TimeUnitSecond
{
    public static final String TAG = TimeUnitSecondInMinute.class.getSimpleName();
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
