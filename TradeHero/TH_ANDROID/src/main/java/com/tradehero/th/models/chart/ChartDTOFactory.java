package com.tradehero.th.models.chart;

import com.tradehero.th.api.security.SecurityCompactDTO;
import android.support.annotation.NonNull;

public interface ChartDTOFactory
{
    @NonNull
    ChartDTO createChartDTO();
    @NonNull
    ChartDTO createChartDTO(SecurityCompactDTO securityCompactDTO);
    @NonNull
    ChartDTO createChartDTO(SecurityCompactDTO securityCompactDTO, ChartSize chartSize);
    @NonNull
    ChartDTO createChartDTO(SecurityCompactDTO securityCompactDTO, ChartSize chartSize, ChartTimeSpan timeSpan);
}
