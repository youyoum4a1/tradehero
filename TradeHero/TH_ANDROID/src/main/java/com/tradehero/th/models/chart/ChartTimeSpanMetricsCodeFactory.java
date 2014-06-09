package com.tradehero.th.models.chart;

import com.sun.istack.internal.NotNull;
import javax.inject.Inject;

public class ChartTimeSpanMetricsCodeFactory
{
    @Inject public ChartTimeSpanMetricsCodeFactory()
    {
        super();
    }

    @NotNull
    public String createCode(@NotNull ChartTimeSpan timeSpan)
    {
        if (timeSpan.duration <= ChartTimeSpan.DAY_1)
        {
            return "1d";
        }
        if (timeSpan.duration <= ChartTimeSpan.DAY_5)
        {
            return "5d";
        }
        if (timeSpan.duration <= ChartTimeSpan.MONTH_3)
        {
            return "3m";
        }
        if (timeSpan.duration <= ChartTimeSpan.MONTH_6)
        {
            return "6m";
        }
        if (timeSpan.duration <= ChartTimeSpan.YEAR_1)
        {
            return "1y";
        }
        if (timeSpan.duration <= ChartTimeSpan.YEAR_2)
        {
            return "2y";
        }
        if (timeSpan.duration <= ChartTimeSpan.YEAR_5)
        {
            return "5y";
        }
        return "max";
    }
}
