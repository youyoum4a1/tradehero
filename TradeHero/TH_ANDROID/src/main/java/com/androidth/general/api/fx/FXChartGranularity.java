package com.androidth.general.api.fx;

import android.support.annotation.NonNull;
import com.androidth.general.models.chart.ChartTimeSpan;

public enum FXChartGranularity
{
    min1("M1", ChartTimeSpan.MIN_1),
    min5("M5", ChartTimeSpan.MIN_5),
    min15("M15", ChartTimeSpan.MIN_15),
    min30("M30", ChartTimeSpan.MIN_30),
    hour1("H1", ChartTimeSpan.HOUR_1),
    hour4("H4", ChartTimeSpan.HOUR_4),
    day1("D", ChartTimeSpan.DAY_1),
    ;

    @NonNull public final String code;
    public final long chartTimeSpanDuration;

    //<editor-fold desc="Constructors">
    private FXChartGranularity(@NonNull String code, long chartTimeSpanDuration)
    {
        this.code = code;
        this.chartTimeSpanDuration = chartTimeSpanDuration;
    }
    //</editor-fold>

    @NonNull public static FXChartGranularity getBestApproximation(@NonNull ChartTimeSpan timeSpan)
    {
        FXChartGranularity previousBest = FXChartGranularity.day1;

        for (FXChartGranularity entry : FXChartGranularity.values())
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
