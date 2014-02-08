package com.tradehero.th.models.chart;

import com.tradehero.th.api.security.SecurityCompactDTO;

/**
 * Created by xavier on 8/2/14.
 */
public interface ChartDTOFactory
{
    ChartDTO createChartDTO();
    ChartDTO createChartDTO(SecurityCompactDTO securityCompactDTO);
    ChartDTO createChartDTO(SecurityCompactDTO securityCompactDTO, ChartSize chartSize);
    ChartDTO createChartDTO(SecurityCompactDTO securityCompactDTO, ChartSize chartSize, ChartTimeSpan timeSpan);
}
