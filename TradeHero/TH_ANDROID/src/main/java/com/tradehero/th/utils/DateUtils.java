package com.tradehero.th.utils;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils
{
    private static final int MILLISECOND_PER_DAY = 1000 * 60 * 60 * 24;

    private static SimpleDateFormat sdf;

    public static String getDisplayableDate(@NonNull Resources resources, @Nullable Date d)
    {
        if (d == null)
        {
            return resources.getString(R.string.na);
        }

        if (sdf == null)
        {
            sdf = new SimpleDateFormat(resources.getString(R.string.data_format_dd_mmm_yyyy_hh_mm));
            sdf.setTimeZone(TimeZone.getDefault());
        }
        return sdf.format(d);
    }

    public static String getDisplayableDate(@NonNull Resources resources, @Nullable Date dStart, @Nullable Date dEnd)
    {
        if (dStart == null || dEnd == null)
        {
            return resources.getString(R.string.na);
        }

        SimpleDateFormat sdfStart = new SimpleDateFormat(resources.getString(R.string.data_format_dd_mmm));
        sdfStart.setTimeZone(TimeZone.getDefault());

        SimpleDateFormat sdfEnd = new SimpleDateFormat(resources.getString(R.string.data_format_dd_mmm_yyyy));
        sdfEnd.setTimeZone(TimeZone.getDefault());

        return sdfStart.format(dStart) + " - " + sdfEnd.format(dEnd);
    }

    public static int getNumberOfDaysBetweenDates(@NonNull Date start, @NonNull Date end)
    {
           return (int) ((end.getTime() - start.getTime()) / MILLISECOND_PER_DAY);
    }

    public static String getFormattedDate(@NonNull Resources resources, @NonNull Date utcDate)
    {
        SimpleDateFormat requiredFormat = new SimpleDateFormat(resources.getString(R.string.data_format_dd_mmm_hh_mm));
        return requiredFormat.format(utcDate);
    }

    public static String getFormattedUtcDate(@NonNull Resources resources, @NonNull Date utcDate)
    {
        SimpleDateFormat requiredFormat = new SimpleDateFormat(resources.getString(R.string.data_format_dd_mmm_yyyy_hh_mm));
        return requiredFormat.format(utcDate);
    }

    public static String getFormattedUtcDateFromDate(@NonNull Resources resources, @NonNull Date date)
    {
        SimpleDateFormat requiredFormat = new SimpleDateFormat(resources.getString(R.string.data_format_yyyy_mm_dd_hh_mm_ss));
        requiredFormat.setTimeZone(TimeZone.getTimeZone("GMT00:00"));
        return requiredFormat.format(date);
    }

    public static String getDurationText(@NonNull Resources resources, int days, int hours, int minutes, int seconds)
    {
        Resources sysRes = Resources.getSystem();

        StringBuilder sb = new StringBuilder();
        int id = 0;
        if (days == 1)
        {
            id = sysRes.getIdentifier("day", "string", "android");
            sb.append("1 ").append(resources.getString(id)).append(" ");
        }
        else if (days > 1)
        {
            id = sysRes.getIdentifier("days", "string", "android");
            sb.append(String.valueOf(days)).append(" ").append(resources.getString(id)).append(" ");
        }

        if (hours > 1)
        {
            id = sysRes.getIdentifier("hours", "string", "android");
        }
        else
        {
            id = sysRes.getIdentifier("hour", "string", "android");
        }
        sb.append(String.valueOf(hours)).append(" ").append(resources.getString(id)).append(" ");

        if (minutes > 1)
        {
            id = sysRes.getIdentifier("minutes", "string", "android");
        }
        else
        {
            id = sysRes.getIdentifier("minute", "string", "android");
        }
        sb.append(String.valueOf(minutes)).append(" ").append(resources.getString(id)).append(" ");

        if (seconds > 1)
        {
            id = sysRes.getIdentifier("seconds", "string", "android");
        }
        else
        {
            id = sysRes.getIdentifier("second", "string", "android");
        }
        sb.append(String.valueOf(seconds)).append(" ").append(resources.getString(id));

        return sb.toString();
    }
}
