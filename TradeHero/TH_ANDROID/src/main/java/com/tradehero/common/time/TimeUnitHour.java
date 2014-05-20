package com.tradehero.common.time;

import org.ocpsoft.prettytime.TimeUnit;

abstract public class TimeUnitHour implements TimeUnit
{
    public static final long MILLIS_PER_HOUR = 3600000;

    public TimeUnitHour()
    {
    }

    @Override public long getMillisPerUnit()
    {
        return MILLIS_PER_HOUR;
    }
}
