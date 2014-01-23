package com.tradehero.common.time;

import org.ocpsoft.prettytime.TimeUnit;

/**
 * Created by xavier on 1/23/14.
 */
abstract public class TimeUnitSecond implements TimeUnit
{
    public static final String TAG = TimeUnitSecond.class.getSimpleName();
    public static final long MILLIS_PER_SECOND = 1000;

    public TimeUnitSecond()
    {
    }

    @Override public long getMillisPerUnit()
    {
        return MILLIS_PER_SECOND;
    }
}
