package com.tradehero.common.time;

import org.ocpsoft.prettytime.Duration;
import org.ocpsoft.prettytime.TimeFormat;

public class TimeFormatFloor implements TimeFormat
{
    @Override public String format(Duration duration)
    {
        return "";
    }

    @Override public String formatUnrounded(Duration duration)
    {
        return "";
    }

    @Override public String decorate(Duration duration, String s)
    {
        return s + format(duration);
    }

    @Override public String decorateUnrounded(Duration duration, String s)
    {
        return s + formatUnrounded(duration);
    }
}
