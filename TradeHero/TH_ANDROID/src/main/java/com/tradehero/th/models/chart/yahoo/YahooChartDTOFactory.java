package com.tradehero.th.models.chart.yahoo;

import android.support.annotation.NonNull;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.models.chart.ChartDTOFactory;
import com.tradehero.th.models.chart.ChartSize;
import com.tradehero.th.models.chart.ChartTimeSpan;
import javax.inject.Inject;

public class YahooChartDTOFactory implements ChartDTOFactory
{
    @Inject public YahooChartDTOFactory()
    {
        super();
    }

    @NonNull
    @Override public YahooChartDTO createChartDTO()
    {
        return new YahooChartDTO();
    }

    @NonNull
    @Override public YahooChartDTO createChartDTO(SecurityCompactDTO securityCompactDTO)
    {
        return new YahooChartDTO(securityCompactDTO);
    }

    @NonNull
    @Override public YahooChartDTO createChartDTO(SecurityCompactDTO securityCompactDTO, ChartSize chartSize)
    {
        return new YahooChartDTO(securityCompactDTO, chartSize);
    }

    @NonNull
    @Override public YahooChartDTO createChartDTO(SecurityCompactDTO securityCompactDTO, ChartSize chartSize, ChartTimeSpan timeSpan)
    {
        return new YahooChartDTO(securityCompactDTO, chartSize, timeSpan);
    }
}
