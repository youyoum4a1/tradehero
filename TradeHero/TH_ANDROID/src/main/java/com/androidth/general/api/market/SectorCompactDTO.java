package com.androidth.general.api.market;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.androidth.general.common.persistence.DTO;

public class SectorCompactDTO implements DTO, WithMarketCap
{
    public int id;
    public String name;
    @Nullable public String imageUrl;
    public double sumMarketCap;

    @JsonIgnore @NonNull public SectorId getSectorId()
    {
        return new SectorId(id);
    }

    @Override public double getSumMarketCap()
    {
        return sumMarketCap;
    }
}
