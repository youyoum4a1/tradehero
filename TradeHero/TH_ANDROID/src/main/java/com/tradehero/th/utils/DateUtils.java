package com.tradehero.th.utils;

import android.content.Context;
import com.tradehero.th.R;
import com.tradehero.th.base.Application;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class DateUtils
{
    private static int MILLISECOND_PER_DAY = 1000 * 60 * 60 * 24;

    private static SimpleDateFormat sdf;

    public static String getDisplayableDate(Context context, Date d)
    {
        if (d == null)
        {
            return context.getString(R.string.na);
        }

        if (sdf == null)
        {
            sdf = new SimpleDateFormat(Application.getResourceString(R.string.data_format_dd_mmm_yyyy_hh_mm_gmt));
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        }
        return sdf.format(d);
    }

    public static int getNumberOfDaysBetweenDates(Date start, Date end)
    {
           return (int) (end.getTime() - start.getTime()) / MILLISECOND_PER_DAY;
    }

    public static String getFormattedDate(Date utcDate)
    {
        SimpleDateFormat requiredFormat = new SimpleDateFormat(Application.getResourceString(R.string.data_format_dd_mmm_hh_mm));
        return requiredFormat.format(utcDate);
    }

    public static String getFormattedUtcDate(Date utcDate)
    {
        SimpleDateFormat requiredFormat = new SimpleDateFormat(Application.getResourceString(R.string.data_format_dd_mmm_yyyy_hh_mm_gmt));
        return requiredFormat.format(utcDate);
    }
}
