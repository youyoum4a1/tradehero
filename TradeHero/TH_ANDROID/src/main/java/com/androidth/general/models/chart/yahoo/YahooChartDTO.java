package com.androidth.general.models.chart.yahoo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.models.chart.ChartDTO;
import com.androidth.general.models.chart.ChartSize;
import com.androidth.general.models.chart.ChartTimeSpan;
import java.util.ArrayList;
import java.util.List;

public class YahooChartDTO implements ChartDTO
{
    public static final ChartSize DEFAULT_CHART_SIZE = new ChartSize(YahooChartSize.small.yahooPixelWidth, YahooChartSize.small.yahooPixelHeight);
    public static final ChartTimeSpan DEFAULT_TIME_SPAN = new ChartTimeSpan(YahooTimeSpan.month3.chartTimeSpanDuration);

    @NonNull public String yahooSymbol;
    @NonNull public YahooChartSize size;
    @NonNull public YahooTimeSpan timeSpan;
    public boolean includeVolume;
    @Nullable public final List<YahooMovingAverageInterval> movingAverageIntervals;

    //<editor-fold desc="Constructors">
    public YahooChartDTO()
    {
        this(null);
    }

    public YahooChartDTO(@Nullable SecurityCompactDTO securityCompactDTO)
    {
        this(
                securityCompactDTO,
                DEFAULT_CHART_SIZE);
    }

    public YahooChartDTO(
            @Nullable SecurityCompactDTO securityCompactDTO,
            @NonNull ChartSize chartSize)
    {
        this(
                securityCompactDTO,
                chartSize,
                DEFAULT_TIME_SPAN);
    }

    public YahooChartDTO(
            @Nullable SecurityCompactDTO securityCompactDTO,
            @NonNull ChartSize chartSize,
            @NonNull ChartTimeSpan chartTimeSpan)
    {
        this(
                securityCompactDTO,
                chartSize,
                chartTimeSpan,
                defaultMovingAverageIntervals());
    }

    public YahooChartDTO(
            @Nullable SecurityCompactDTO securityCompactDTO,
            @NonNull ChartSize chartSize,
            @NonNull ChartTimeSpan chartTimeSpan,
            @Nullable List<YahooMovingAverageInterval> movingAverageIntervals)
    {
        setSecurityCompactDTO(securityCompactDTO);
        setChartSize(chartSize);
        setChartTimeSpan(chartTimeSpan);
        this.movingAverageIntervals = movingAverageIntervals;
    }
    //</editor-fold>

    @Override public void setSecurityCompactDTO(@Nullable SecurityCompactDTO securityCompactDTO)
    {
        this.yahooSymbol = securityCompactDTO == null ? "" : securityCompactDTO.yahooSymbol;
    }

    @NonNull @Override public ChartSize getChartSize()
    {
        return size.getChartSize();
    }

    @Override public void setChartSize(@NonNull ChartSize chartSize)
    {
        this.size = YahooChartSize.getPreferredSize(chartSize.width, chartSize.height);
    }

    @NonNull @Override public ChartTimeSpan getChartTimeSpan()
    {
        return timeSpan.getChartTimeSpan();
    }

    @Override public void setChartTimeSpan(@NonNull ChartTimeSpan chartTimeSpan)
    {
        this.timeSpan = YahooTimeSpan.getBestApproximation(chartTimeSpan);
    }

    @Override public void setIncludeVolume(boolean includeVolume)
    {
        this.includeVolume = includeVolume;
    }

    @Override public boolean isIncludeVolume()
    {
        return includeVolume;
    }

    @NonNull public static List<YahooMovingAverageInterval> defaultMovingAverageIntervals()
    {
        ArrayList<YahooMovingAverageInterval> created = new ArrayList<>();
        created.add(YahooMovingAverageInterval.m50);
        created.add(YahooMovingAverageInterval.m200);
        return created;
    }

    @NonNull public String getChartUrl()
    {
        return String.format(
                "http://chart.finance.yahoo.com/z?s=%s&t=%s&q=l&z=%s&p=%s%s",
                yahooSymbol,
                timeSpan.code,
                size.code,
                YahooMovingAverageInterval.concat(movingAverageIntervals),
                includeVolume ? "&a=v" : "");
    }
}
