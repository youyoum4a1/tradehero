package com.tradehero.common.time;

import org.ocpsoft.prettytime.TimeUnit;


abstract public class TimeUnitDay implements TimeUnit
{
    public static final String TAG = TimeUnitDay.class.getSimpleName();
    public static final long MILLIS_PER_DAY = 86400000;

    public TimeUnitDay()
    {
    }

    @Override public long getMillisPerUnit()
    {
        return MILLIS_PER_DAY;
    }
}
