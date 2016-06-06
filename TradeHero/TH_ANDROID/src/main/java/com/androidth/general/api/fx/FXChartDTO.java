package com.androidth.general.api.fx;

import com.androidth.general.common.persistence.DTO;
import java.util.List;

public class FXChartDTO implements DTO
{
    public String instrument;
    public String granularity;
    public List<FXCandleDTO> candles;

    @Override
    public String toString() {
        return "FXChartDTO{" +
                "instrument='" + instrument + '\'' +
                ", granularity='" + granularity + '\'' +
                '}';
    }
}
