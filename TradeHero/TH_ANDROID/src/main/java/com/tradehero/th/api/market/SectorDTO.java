package com.tradehero.th.api.market;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.common.persistence.DTO;

public class SectorDTO implements DTO
{
    public int id;
    public String name;
    @Nullable public String logoUrl;
    public double sumMarketCap;
    @Nullable public SecuritySuperCompactDTOList topSecurities;

    @JsonIgnore @NonNull public SectorId getSectorId()
    {
        return new SectorId(id);
    }
}
