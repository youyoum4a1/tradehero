package com.tradehero.th.models.chart;

import com.tradehero.th.api.security.SecurityCompactDTO;
import org.jetbrains.annotations.NotNull;

public interface ChartDTOFactory
{
    @NotNull
    ChartDTO createChartDTO();
    @NotNull
    ChartDTO createChartDTO(SecurityCompactDTO securityCompactDTO);
    @NotNull
    ChartDTO createChartDTO(SecurityCompactDTO securityCompactDTO, ChartSize chartSize);
    @NotNull
    ChartDTO createChartDTO(SecurityCompactDTO securityCompactDTO, ChartSize chartSize, ChartTimeSpan timeSpan);
}
