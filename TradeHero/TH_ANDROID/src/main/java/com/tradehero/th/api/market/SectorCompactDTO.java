package com.tradehero.th.api.market;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.common.persistence.DTO;
import org.jetbrains.annotations.NotNull;

public class SectorCompactDTO implements DTO
{
    public int id;
    public String name;

    @JsonIgnore @NotNull public SectorId getSectorId()
    {
        return new SectorId(id);
    }
}
