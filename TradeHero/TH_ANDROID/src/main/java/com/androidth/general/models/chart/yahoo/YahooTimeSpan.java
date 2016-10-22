package com.androidth.general.models.chart.yahoo;

import android.support.annotation.NonNull;
import com.androidth.general.models.chart.ChartTimeSpan;

public enum YahooTimeSpan
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
    YahooTimeSpan(@NonNull String c, long chartTimeSpanDuration)
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

    @NonNull public static YahooTimeSpan getBestApproximation(@NonNull ChartTimeSpan timeSpan)
    {
        YahooTimeSpan previousBest = YahooTimeSpan.day1;

        for (YahooTimeSpan entry : YahooTimeSpan.values())
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