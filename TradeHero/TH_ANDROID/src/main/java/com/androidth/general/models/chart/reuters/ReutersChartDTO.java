package com.androidth.general.models.chart.reuters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.models.chart.ChartDTO;
import com.androidth.general.models.chart.ChartSize;
import com.androidth.general.models.chart.ChartTimeSpan;
import com.androidth.general.models.chart.reuters.ReutersChartSize;
import com.androidth.general.models.chart.reuters.ReutersMovingAverageInterval;
import com.androidth.general.models.chart.reuters.ReutersTimeSpan;

import java.util.ArrayList;
import java.util.List;

public class ReutersChartDTO implements ChartDTO {
    public static final ChartSize DEFAULT_CHART_SIZE = new ChartSize(ReutersChartSize.small.reutersPixelWidth, ReutersChartSize.small.reutersPixelHeight);
    public static final ChartTimeSpan DEFAULT_TIME_SPAN = new ChartTimeSpan(ReutersTimeSpan.month3.chartTimeSpanDuration);

    @NonNull
    public String reutersSymbol;
    @NonNull
    public ReutersChartSize size;
    @NonNull
    public ReutersTimeSpan timeSpan;
    public boolean includeVolume;
    @Nullable
    public final List<ReutersMovingAverageInterval> movingAverageIntervals;

    //<editor-fold desc="Constructors">
    public ReutersChartDTO() {
        this(null);
    }

    public ReutersChartDTO(@Nullable SecurityCompactDTO securityCompactDTO) {
        this(
                securityCompactDTO,
                DEFAULT_CHART_SIZE);
    }

    public ReutersChartDTO(
            @Nullable SecurityCompactDTO securityCompactDTO,
            @NonNull ChartSize chartSize) {
        this(
                securityCompactDTO,
                chartSize,
                DEFAULT_TIME_SPAN);
    }

    public ReutersChartDTO(
            @Nullable SecurityCompactDTO securityCompactDTO,
            @NonNull ChartSize chartSize,
            @NonNull ChartTimeSpan chartTimeSpan) {
        this(
                securityCompactDTO,
                chartSize,
                chartTimeSpan,
                (securityCompactDTO != null && securityCompactDTO.secTypeDesc != null && securityCompactDTO.secTypeDesc.toLowerCase().equals("warrant"))
                        ? defaultMovingAverageIntervalsForWarrants() : defaultMovingAverageIntervals());
    }

    public ReutersChartDTO(
            @Nullable SecurityCompactDTO securityCompactDTO,
            @NonNull ChartSize chartSize,
            @NonNull ChartTimeSpan chartTimeSpan,
            @Nullable List<ReutersMovingAverageInterval> movingAverageIntervals) {
        setSecurityCompactDTO(securityCompactDTO);
        setChartSize(chartSize);
        setChartTimeSpan(chartTimeSpan);
        this.movingAverageIntervals = movingAverageIntervals;
    }
    //</editor-fold>

    @Override
    public void setSecurityCompactDTO(@Nullable SecurityCompactDTO securityCompactDTO) {
        this.reutersSymbol = securityCompactDTO == null ? "" : securityCompactDTO.reutersSymbol;
    }

    @NonNull
    @Override
    public ChartSize getChartSize() {
        return size.getChartSize();
    }

    @Override
    public void setChartSize(@NonNull ChartSize chartSize) {
        this.size = ReutersChartSize.getPreferredSize(chartSize.width, chartSize.height);
    }

    @NonNull
    @Override
    public ChartTimeSpan getChartTimeSpan() {
        return timeSpan.getChartTimeSpan();
    }

    @Override
    public void setChartTimeSpan(@NonNull ChartTimeSpan chartTimeSpan) {
        this.timeSpan = ReutersTimeSpan.getBestApproximation(chartTimeSpan);
    }

    @Override
    public void setIncludeVolume(boolean includeVolume) {
        this.includeVolume = includeVolume;
    }

    @Override
    public boolean isIncludeVolume() {
        return includeVolume;
    }

    @NonNull
    public static List<ReutersMovingAverageInterval> defaultMovingAverageIntervals() {
        ArrayList<ReutersMovingAverageInterval> created = new ArrayList<>();
        created.add(ReutersMovingAverageInterval.m50);
        created.add(ReutersMovingAverageInterval.m200);
        return created;
    }

    @NonNull
    public static List<ReutersMovingAverageInterval> defaultMovingAverageIntervalsForWarrants() {
        ArrayList<ReutersMovingAverageInterval> created = new ArrayList<>();
        created.add(ReutersMovingAverageInterval.m5);
        created.add(ReutersMovingAverageInterval.m10);
        created.add(ReutersMovingAverageInterval.m15);
        return created;
    }

    @NonNull
    public String getChartUrl() {
        return String.format(
                "http://charts.reuters.com/reuters/enhancements/chartapi/chart_api.asp?symbol=%s&duration=%s&width=%s&height=%s",
                reutersSymbol,
                timeSpan.code,
                Integer.toString(size.reutersPixelWidth),
                Integer.toString(size.reutersPixelHeight)
//                ReutersMovingAverageInterval.concat(movingAverageIntervals),
//                includeVolume ? "&a=v" : ""
        );
    }
}
