package com.tradehero.th.utils;

import android.content.res.Resources;
import com.tradehero.th.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DateUtils
{
    private static final int MILLISECOND_PER_DAY = 1000 * 60 * 60 * 24;

    private static SimpleDateFormat sdf;

    public static String getDisplayableDate(@NotNull Resources resources, @Nullable Date d)
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

    public static String getDisplayableDate(@NotNull Resources resources, @Nullable Date dStart, @Nullable Date dEnd)
    {
        if (dStart == null || dEnd == null)
        {
            return resources.getString(R.string.na);
        }

        SimpleDateFormat sdfStart = new SimpleDateFormat(resources.getString(R.string.data_format_dd_mmm));
        sdfStart.setTimeZone(TimeZone.getDefault());

        SimpleDateFormat sdfEnd = new SimpleDateFormat(resources.getString(R.string.data_format_dd_mmm));
        sdfEnd.setTimeZone(TimeZone.getDefault());

        return sdfStart.format(dStart) + " - " + sdfEnd.format(dEnd);
    }

    public static int getNumberOfDaysBetweenDates(@NotNull Date start, @NotNull Date end)
    {
           return (int) (end.getTime() - start.getTime()) / MILLISECOND_PER_DAY;
    }

    public static String getFormattedDate(@NotNull Resources resources, @NotNull Date utcDate)
    {
        SimpleDateFormat requiredFormat = new SimpleDateFormat(resources.getString(R.string.data_format_dd_mmm_hh_mm));
        return requiredFormat.format(utcDate);
    }

    public static String getFormattedUtcDate(@NotNull Resources resources, @NotNull Date utcDate)
    {
        SimpleDateFormat requiredFormat = new SimpleDateFormat(resources.getString(R.string.data_format_dd_mmm_yyyy_hh_mm));
        return requiredFormat.format(utcDate);
    }

    public static String getFormattedUtcDateFromDate(@NotNull Resources resources, @NotNull Date date)
    {
        SimpleDateFormat requiredFormat = new SimpleDateFormat(resources.getString(R.string.data_format_yyyy_mm_dd_hh_mm_ss));
        requiredFormat.setTimeZone(TimeZone.getTimeZone("GMT00:00"));
        return requiredFormat.format(date);
    }
}
