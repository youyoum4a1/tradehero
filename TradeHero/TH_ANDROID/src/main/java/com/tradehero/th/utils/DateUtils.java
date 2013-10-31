package com.tradehero.th.utils;

import android.content.Context;
import com.tradehero.th.R;

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
            return context.getString(R.string.na);

        return sdf.format(d);
    }

    public static int getNumberOfDaysBetweenDates(Date start, Date end)
    {
           return (int)(end.getTime() - start.getTime())/MILLISECOND_PER_DAY;
    }

}
