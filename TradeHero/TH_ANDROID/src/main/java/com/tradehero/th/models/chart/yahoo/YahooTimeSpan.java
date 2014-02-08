package com.tradehero.th.models.chart.yahoo;

import com.tradehero.th.R;
import com.tradehero.th.models.chart.ChartTimeSpan;

import java.util.Map;

/**
 * Created by julien on 9/10/13
 */
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

    public final String code;
    public final int stringResId;
    public final long chartTimeSpanDuration;

    private static Map<Long, YahooTimeSpan> timeSpans;

    private YahooTimeSpan(String c, int stringResId, long chartTimeSpanDuration)
    {
        code = c;
        this.stringResId = stringResId;
        this.chartTimeSpanDuration = chartTimeSpanDuration;
    }

    public boolean equalsCode(String otherCode)
    {
        return (otherCode != null) && otherCode.equals(code);
    }

    public ChartTimeSpan getChartTimeSpan()
    {
        return new ChartTimeSpan(chartTimeSpanDuration);
    }

    public String toString()
    {
        return code;
    }

    public static YahooTimeSpan getBestApproximation(ChartTimeSpan timeSpan)
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