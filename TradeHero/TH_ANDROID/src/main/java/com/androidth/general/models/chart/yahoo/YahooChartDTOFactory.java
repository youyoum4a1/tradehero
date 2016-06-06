package com.androidth.general.models.chart.yahoo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.models.chart.ChartDTOFactory;
import com.androidth.general.models.chart.ChartSize;
import com.androidth.general.models.chart.ChartTimeSpan;
import javax.inject.Inject;

public class YahooChartDTOFactory implements ChartDTOFactory
{
    //<editor-fold desc="Constructors">
    @Inject public YahooChartDTOFactory()
    {
        super();
    }
    //</editor-fold>

    @NonNull
    @Override public YahooChartDTO createChartDTO()
    {
        return new YahooChartDTO();
    }

    @NonNull
    @Override public YahooChartDTO createChartDTO(@Nullable SecurityCompactDTO securityCompactDTO)
    {
        return new YahooChartDTO(securityCompactDTO);
    }

    @NonNull
    @Override public YahooChartDTO createChartDTO(
            @Nullable SecurityCompactDTO securityCompactDTO,
            @NonNull ChartSize chartSize)
    {
        return new YahooChartDTO(securityCompactDTO, chartSize);
    }

    @NonNull
    @Override public YahooChartDTO createChartDTO(
            @Nullable SecurityCompactDTO securityCompactDTO,
            @NonNull ChartSize chartSize,
            @NonNull ChartTimeSpan timeSpan)
    {
        return new YahooChartDTO(securityCompactDTO, chartSize, timeSpan);
    }
}
