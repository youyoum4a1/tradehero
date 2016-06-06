package com.androidth.general.models.chart;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.api.security.SecurityCompactDTO;

public interface ChartDTO extends DTO
{
    void setSecurityCompactDTO(@Nullable SecurityCompactDTO securityCompactDTO);
    @NonNull ChartSize getChartSize();
    void setChartSize(@NonNull ChartSize chartSize);
    @NonNull ChartTimeSpan getChartTimeSpan();
    void setIncludeVolume(boolean includeVolume);
    boolean isIncludeVolume();
    void setChartTimeSpan(@NonNull ChartTimeSpan chartTimeSpan);
    @NonNull String getChartUrl();
}
