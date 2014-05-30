package com.tradehero.th.models.chart;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.security.SecurityCompactDTO;

public interface ChartDTO extends DTO
{
    void setSecurityCompactDTO(SecurityCompactDTO securityCompactDTO);
    ChartSize getChartSize();
    void setChartSize(ChartSize chartSize);
    ChartTimeSpan getChartTimeSpan();
    void setIncludeVolume(boolean includeVolume);
    boolean isIncludeVolume();
    void setChartTimeSpan(ChartTimeSpan chartTimeSpan);
    String getChartUrl();
}
