package com.tradehero.th.models.chart;

import com.tradehero.th.api.security.SecurityCompactDTO;

/**
 * Created by xavier on 8/2/14.
 */
public interface ChartDTOFactory
{
    ChartDTO createChartDTO(SecurityCompactDTO securityCompactDTO, ChartTimeSpan timeSpan);
}
