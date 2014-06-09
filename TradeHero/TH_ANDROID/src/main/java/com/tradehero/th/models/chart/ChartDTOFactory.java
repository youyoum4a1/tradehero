package com.tradehero.th.models.chart;

import com.sun.istack.internal.NotNull;
import com.tradehero.th.api.security.SecurityCompactDTO;

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
