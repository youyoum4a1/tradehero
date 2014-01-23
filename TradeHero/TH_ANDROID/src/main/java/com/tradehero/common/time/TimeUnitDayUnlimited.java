package com.tradehero.common.time;

/**
 * Created by xavier on 1/23/14.
 */
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
