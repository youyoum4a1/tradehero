package com.tradehero.th.models.chart.yahoo;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.tradehero.th.R;
import com.tradehero.th.models.chart.ChartTimeSpan;

public enum YahooTimeSpan
{
    day1("1d", R.string.yahoo_chart_1d, ChartTimeSpan.DAY_1),
    day5("5d", R.string.yahoo_chart_5d, ChartTimeSpan.DAY_5),
    month3("3m", R.string.yahoo_chart_3m, ChartTimeSpan.MONTH_3),
    month6("6m", R.string.yahoo_chart_6m, ChartTimeSpan.MONTH_6),
    year1("1y", R.string.yahoo_chart_1y, ChartTimeSpan.YEAR_1),
    year2("2y", R.string.yahoo_chart_2y, ChartTimeSpan.YEAR_2),
    year5("5y", R.string.yahoo_chart_5y, ChartTimeSpan.YEAR_5),
    yearMax("my", R.string.yahoo_chart_max, ChartTimeSpan.MAX);

    @NonNull public final String code;
    @StringRes public final int stringResId;
    public final long chartTimeSpanDuration;

    //<editor-fold desc="Constructors">
    private YahooTimeSpan(@NonNull String c, @StringRes int stringResId, long chartTimeSpanDuration)
    {
        code = c;
        this.stringResId = stringResId;
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