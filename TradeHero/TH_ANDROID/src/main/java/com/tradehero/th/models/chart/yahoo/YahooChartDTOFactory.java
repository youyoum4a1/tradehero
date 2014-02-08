package com.tradehero.th.models.chart.yahoo;

import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.models.chart.ChartDTOFactory;
import com.tradehero.th.models.chart.ChartTimeSpan;

/**
 * Created by xavier on 8/2/14.
 */
public class YahooChartDTOFactory implements ChartDTOFactory
{
    @Override public YahooChartDTO createChartDTO(SecurityCompactDTO securityCompactDTO, ChartTimeSpan timeSpan)
    {
        throw new IllegalArgumentException("Not implemented");
    }
}
