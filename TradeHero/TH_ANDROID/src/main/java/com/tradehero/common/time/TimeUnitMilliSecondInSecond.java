package com.tradehero.common.time;

/**
 * Created by xavier on 1/23/14.
 */
public class TimeUnitMilliSecondInSecond extends TimeUnitMilliSecond
{
    public static final String TAG = TimeUnitMilliSecondInSecond.class.getSimpleName();
    public static final long MAX_MILLISECOND_IN_MILLISEC = 999;

    public TimeUnitMilliSecondInSecond()
    {
    }

    @Override public long getMaxQuantity()
    {
        return MAX_MILLISECOND_IN_MILLISEC;
    }
}
