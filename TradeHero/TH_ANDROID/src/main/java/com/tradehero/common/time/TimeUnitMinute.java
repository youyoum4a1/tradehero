package com.tradehero.common.time;

import org.ocpsoft.prettytime.TimeUnit;

/**
 * Created by xavier on 1/23/14.
 */
abstract public class TimeUnitMinute implements TimeUnit
{
    public static final String TAG = TimeUnitMinute.class.getSimpleName();
    public static final long MILLIS_PER_MINUTE = 60000;

    public TimeUnitMinute()
    {
    }

    @Override public long getMillisPerUnit()
    {
        return MILLIS_PER_MINUTE;
    }
}
