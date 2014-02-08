package com.tradehero.th.models.chart;

/**
 * Created by xavier on 8/2/14.
 */
public class ChartTimeSpan
{
    public static final String TAG = ChartTimeSpan.class.getSimpleName();

    public static final long DAY_1 =     86400;
    public static final long DAY_5 =    432000;
    public static final long MONTH_3 = 2629800;
    public static final long MONTH_6 = 5259600;
    public static final long YEAR_1 = 10519200;
    public static final long YEAR_2 = 21038400;
    public static final long YEAR_5 = 52596000;
    public static final long MAX = Long.MAX_VALUE;

    public final long duration;

    public ChartTimeSpan(long duration)
    {
        super();
        this.duration = duration;
    }
}
