package com.androidth.general.api.position;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.api.security.SecurityId;

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
