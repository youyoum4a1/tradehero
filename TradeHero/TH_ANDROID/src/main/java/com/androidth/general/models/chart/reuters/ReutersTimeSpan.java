package com.androidth.general.models.chart.reuters;

import android.support.annotation.NonNull;
import com.androidth.general.models.chart.ChartTimeSpan;

public enum ReutersTimeSpan
{
    day1("1d", ChartTimeSpan.DAY_1),
    day5("5d", ChartTimeSpan.DAY_5),
    month3("3m", ChartTimeSpan.MONTH_3),
    month6("6m", ChartTimeSpan.MONTH_6),
    year1("1y", ChartTimeSpan.YEAR_1),
    year2("2y", ChartTimeSpan.YEAR_2),
    year5("5y", ChartTimeSpan.YEAR_5),
    yearMax("my", ChartTimeSpan.MAX);

    @NonNull public final String code;
    public final long chartTimeSpanDuration;

    //<editor-fold desc="Constructors">
    private ReutersTimeSpan(@NonNull String c, long chartTimeSpanDuration)
    {
        code = c;
        this.chartTimeSpanDuration = chartTimeSpanDuration;
    }
    //</editor-fold>

    @NonNull public ChartTimeSpan getChartTimeSpan()
    {
        return new ChartTimeSpan(chartTimeSpanDuration);
    }

    public String toString()
    {
        return code;
    }

    @NonNull public static ReutersTimeSpan getBestApproximation(@NonNull ChartTimeSpan timeSpan)
    {
        ReutersTimeSpan previousBest = ReutersTimeSpan.day1;

        for (ReutersTimeSpan entry : ReutersTimeSpan.values())
        {
            if (timeSpan.duration <= entry.chartTimeSpanDuration)
            {
                return entry;
            }
            previousBest = entry;
        }

        return previousBest;
    }
}