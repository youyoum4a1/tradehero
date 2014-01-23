package com.tradehero.common.time;

import org.ocpsoft.prettytime.TimeUnit;

/**
 * Created by xavier on 1/23/14.
 */
public class TimeUnitSecondInMinute implements TimeUnit
{
    public static final String TAG = TimeUnitSecondInMinute.class.getSimpleName();
    public static final long MILLIS_PER_SECOND = 1000;
    public static final long MAX_SECOND_IN_MINUTE = 59;

    public TimeUnitSecondInMinute()
    {
    }

    @Override public long getMillisPerUnit()
    {
        return MILLIS_PER_SECOND;
    }

    @Override public long getMaxQuantity()
    {
        return MAX_SECOND_IN_MINUTE;
    }
}
