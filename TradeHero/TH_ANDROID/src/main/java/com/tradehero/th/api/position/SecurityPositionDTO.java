package com.tradehero.th.api.position;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;

public class SecurityPositionDTO implements DTO
{
    @NonNull public SecurityCompactDTO security;
    @Nullable public PositionDTOCompactList positions;
    //public PositionDTOCompact position; // This is a backward compatible element. Do not add back
    public int firstTradeAllTime;

    //<editor-fold desc="Constructors">
    SecurityPositionDTO()
    {
    }

    public SecurityPositionDTO(
            @NonNull SecurityCompactDTO security,
            @Nullable PositionDTOCompactList positions,
            int firstTradeAllTime)
    {
        this.security = security;
        this.positions = positions;
        this.firstTradeAllTime = firstTradeAllTime;
    }
    //</editor-fold>

    @NonNull public SecurityId getSecurityId()
    {
        return security.getSecurityId();
    }
}
