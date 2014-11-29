package com.tradehero.th.api.market;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.common.persistence.DTO;

public class SectorCompactDTO implements DTO
{
    public int id;
    public String name;

    @JsonIgnore @NonNull public SectorId getSectorId()
    {
        return new SectorId(id);
    }
}
