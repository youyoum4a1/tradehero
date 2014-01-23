package com.tradehero.common.time;

import org.ocpsoft.prettytime.TimeUnit;

/**
 * Created by xavier on 1/23/14.
 */
abstract public class TimeUnitHour implements TimeUnit
{
    public static final String TAG = TimeUnitHour.class.getSimpleName();
    public static final long MILLIS_PER_HOUR = 3600000;

    public TimeUnitHour()
    {
    }

    @Override public long getMillisPerUnit()
    {
        return MILLIS_PER_HOUR;
    }
}
