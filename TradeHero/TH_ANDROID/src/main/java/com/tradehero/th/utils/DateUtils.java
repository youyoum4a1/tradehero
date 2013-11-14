package com.tradehero.th.utils;

import android.content.Context;
import com.tradehero.th.R;

import com.tradehero.th.base.Application;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by julien on 31/10/13
 */
public class DateUtils
{
    private static int MILLISECOND_PER_DAY = 1000 * 60 * 60 * 24;

    private static SimpleDateFormat sdf = new SimpleDateFormat("d MMM H:m z");
    static {
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public static String getDisplayableDate(Context context, Date d)
    {
        if (d == null)
        {
            return context.getString(R.string.na);
        }

        return sdf.format(d);
    }

    public static int getNumberOfDaysBetweenDates(Date start, Date end)
    {
           return (int) (end.getTime() - start.getTime()) / MILLISECOND_PER_DAY;
    }

    public static String getFormattedUtcDate(Date utcDate)
    {
        SimpleDateFormat requiredFormat = new SimpleDateFormat(Application.getResourceString(R.string.date_format_gmt_day_and_time));
        return requiredFormat.format(utcDate);
    }
}
