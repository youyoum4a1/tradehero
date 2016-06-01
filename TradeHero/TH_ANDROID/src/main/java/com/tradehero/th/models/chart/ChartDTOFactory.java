package com.ayondo.academy.models.chart;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.ayondo.academy.api.security.SecurityCompactDTO;

public interface ChartDTOFactory
{
    @NonNull
    ChartDTO createChartDTO();
    @NonNull
    ChartDTO createChartDTO(@Nullable SecurityCompactDTO securityCompactDTO);
    @NonNull
    ChartDTO createChartDTO(@Nullable SecurityCompactDTO securityCompactDTO, @NonNull ChartSize chartSize);
    @NonNull
    ChartDTO createChartDTO(@Nullable SecurityCompactDTO securityCompactDTO, @NonNull ChartSize chartSize, @NonNull ChartTimeSpan timeSpan);
}
