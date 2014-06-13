package com.tradehero.common.time;

import org.ocpsoft.prettytime.TimeUnit;

abstract public class TimeUnitMilliSecond implements TimeUnit
{
    public static final long MILLIS_PER_MILLISECOND = 1;

    public TimeUnitMilliSecond()
    {
    }

    @Override public long getMillisPerUnit()
    {
        return MILLIS_PER_MILLISECOND;
    }
}
