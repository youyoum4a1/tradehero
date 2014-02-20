package com.tradehero.th.models.chart;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.security.SecurityCompactDTO;

/**
 * Created by xavier on 1/30/14.
 */
public interface ChartDTO extends DTO
{
    void setSecurityCompactDTO(SecurityCompactDTO securityCompactDTO);
    ChartSize getChartSize();
    void setChartSize(ChartSize chartSize);
    ChartTimeSpan getChartTimeSpan();
    void setChartTimeSpan(ChartTimeSpan chartTimeSpan);
    String getChartUrl();
}
