package com.tradehero.th.models.chart;

import com.tradehero.th.api.security.SecurityCompactDTO;

public interface ChartDTOFactory
{
    ChartDTO createChartDTO();
    ChartDTO createChartDTO(SecurityCompactDTO securityCompactDTO);
    ChartDTO createChartDTO(SecurityCompactDTO securityCompactDTO, ChartSize chartSize);
    ChartDTO createChartDTO(SecurityCompactDTO securityCompactDTO, ChartSize chartSize, ChartTimeSpan timeSpan);
}
