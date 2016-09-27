package com.androidth.general.models.chart.reuters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.models.chart.ChartDTOFactory;
import com.androidth.general.models.chart.ChartSize;
import com.androidth.general.models.chart.ChartTimeSpan;
import javax.inject.Inject;

public class ReutersChartDTOFactory implements ChartDTOFactory
{
    //<editor-fold desc="Constructors">
    @Inject public ReutersChartDTOFactory()
    {
        super();
    }
    //</editor-fold>

    @NonNull
    @Override public ReutersChartDTO createChartDTO()
    {
        return new ReutersChartDTO();
    }

    @NonNull
    @Override public ReutersChartDTO createChartDTO(@Nullable SecurityCompactDTO securityCompactDTO)
    {
        return new ReutersChartDTO(securityCompactDTO);
    }

    @NonNull
    @Override public ReutersChartDTO createChartDTO(
            @Nullable SecurityCompactDTO securityCompactDTO,
            @NonNull ChartSize chartSize)
    {
        return new ReutersChartDTO(securityCompactDTO, chartSize);
    }

    @NonNull
    @Override public ReutersChartDTO createChartDTO(
            @Nullable SecurityCompactDTO securityCompactDTO,
            @NonNull ChartSize chartSize,
            @NonNull ChartTimeSpan timeSpan)
    {
        return new ReutersChartDTO(securityCompactDTO, chartSize, timeSpan);
    }
}
