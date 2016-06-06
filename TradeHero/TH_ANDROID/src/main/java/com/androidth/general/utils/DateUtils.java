package com.androidth.general.utils;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import com.androidth.general.R;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils
{
    private static final int MILLISECOND_PER_DAY = 1000 * 60 * 60 * 24;

    private static SimpleDateFormat sdf;

    public static String getDisplayableDate(@NonNull Resources resources, @Nullable Date d)
    {
        return getDisplayableDate(resources, d, R.string.data_format_dd_mmm_yyyy_hh_mm);
    }

    public static String getDisplayableDate(@NonNull Resources resources, @Nullable Date d, @StringRes int patternResId)
    {
        if (d == null)
        {
            return resources.getString(R.string.na);
        }

        String pattern = resources.getString(patternResId);
        return getDisplayableDate(d, pattern);
    }

    public static String getDisplayableDate(@NonNull Date d, @NonNull String pattern)
    {
        initFormatter(pattern);
        return sdf.format(d);
    }

    protected static void initFormatter(@NonNull String pattern)
    {
        if (sdf == null)
        {
            sdf = new SimpleDateFormat(pattern);
            sdf.setTimeZone(TimeZone.getDefault());
        }
        else if (!sdf.toPattern().equals(pattern))
        {
            sdf.applyPattern(pattern);
        }
    }

    public static String getDisplayableDate(@NonNull Resources resources, @Nullable Date dStart, @Nullable Date dEnd)
    {
        if (dStart == null || dEnd == null)
        {
            return resources.getString(R.string.na);
        }
        Calendar cs = Calendar.getInstance();
        Calendar ce = Calendar.getInstance();
        cs.setTime(dStart);
        ce.setTime(dEnd);

        boolean sameYear = cs.get(Calendar.YEAR) == ce.get(Calendar.YEAR);

        SimpleDateFormat sdfStart =
                new SimpleDateFormat(resources.getString(sameYear ? R.string.data_format_dd_mmm : R.string.data_format_dd_mmm_yyyy));
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

    public static String getDurationText(@NonNull Resources resources, int days, int hours, int minutes)
    {
        Resources sysRes = Resources.getSystem();

        //TODO use plurals
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

        return sb.toString();
    }

    public static @Nullable Date parseString(String stringToParse, String pattern)
    {
        initFormatter(pattern);
        try
        {
            return sdf.parse(stringToParse);
        } catch (ParseException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static @Nullable Date parseString(Resources resources, String stringToParse, @StringRes int patternResId)
    {
        return parseString(stringToParse, resources.getString(patternResId));
    }
}
