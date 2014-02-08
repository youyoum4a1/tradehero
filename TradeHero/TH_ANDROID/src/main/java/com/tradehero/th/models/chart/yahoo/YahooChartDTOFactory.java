package com.tradehero.th.models.chart.yahoo;

import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.models.chart.ChartDTOFactory;
import com.tradehero.th.models.chart.ChartSize;
import com.tradehero.th.models.chart.ChartTimeSpan;

/**
 * Created by xavier on 8/2/14.
 */
public class YahooChartDTOFactory implements ChartDTOFactory
{
    @Override public YahooChartDTO createChartDTO()
    {
        return new YahooChartDTO();
    }

    @Override public YahooChartDTO createChartDTO(SecurityCompactDTO securityCompactDTO)
    {
        return new YahooChartDTO(securityCompactDTO);
    }

    @Override public YahooChartDTO createChartDTO(SecurityCompactDTO securityCompactDTO, ChartSize chartSize)
    {
        return new YahooChartDTO(securityCompactDTO, chartSize);
    }

    @Override public YahooChartDTO createChartDTO(SecurityCompactDTO securityCompactDTO, ChartSize chartSize, ChartTimeSpan timeSpan)
    {
        return new YahooChartDTO(securityCompactDTO, chartSize, timeSpan);
    }
}
