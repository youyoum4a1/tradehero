package com.tradehero.th.models.chart;

import android.support.annotation.NonNull;
import com.tradehero.th.api.security.SecurityCompactDTO;

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
