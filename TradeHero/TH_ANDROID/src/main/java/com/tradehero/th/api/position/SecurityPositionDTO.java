package com.ayondo.academy.api.position;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.common.persistence.DTO;
import com.ayondo.academy.api.security.SecurityCompactDTO;
import com.ayondo.academy.api.security.SecurityId;

public class SecurityPositionDTO implements DTO
{
    @NonNull public SecurityCompactDTO security;
    @Nullable public PositionDTOCompactList positions;
    //public PositionDTOCompact position; // This is a backward compatible element. Do not add back
    public int firstTradeAllTime;

    //<editor-fold desc="Constructors">
    public SecurityPositionDTO()
    {
    }
    //</editor-fold>

    @JsonIgnore
    @NonNull public SecurityId getSecurityId()
    {
        return security.getSecurityId();
    }
}
