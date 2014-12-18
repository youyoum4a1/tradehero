package com.tradehero.th.models.chart.yahoo;

import com.tradehero.th.R;
import com.tradehero.th.models.chart.ChartTimeSpan;

public enum YahooTimeSpan
{
    min1("M1", R.string.yahoo_chart_1min, ChartTimeSpan.MIN_1),
    min5("M5", R.string.yahoo_chart_5min, ChartTimeSpan.MIN_5),
    min15("M15", R.string.yahoo_chart_15min, ChartTimeSpan.MIN_15),
    min30("M30", R.string.yahoo_chart_30min, ChartTimeSpan.MIN_30),
    hour1("H1", R.string.yahoo_chart_1h, ChartTimeSpan.HOUR_1),
    hour4("H4", R.string.yahoo_chart_4h, ChartTimeSpan.HOUR_4),
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