package com.tradehero.th.api.position;

import android.support.annotation.Nullable;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import org.jetbrains.annotations.NotNull;

public class SecurityPositionDTO implements DTO
{
    @NotNull public SecurityCompactDTO security;
    @Nullable public PositionDTOCompactList positions;
    //public PositionDTOCompact position; // This is a backward compatible element. Do not add back
    public int firstTradeAllTime;

    //<editor-fold desc="Constructors">
    SecurityPositionDTO()
    {
    }

    public SecurityPositionDTO(
            @NotNull SecurityCompactDTO security,
            @Nullable PositionDTOCompactList positions,
            int firstTradeAllTime)
    {
        this.security = security;
        this.positions = positions;
        this.firstTradeAllTime = firstTradeAllTime;
    }
    //</editor-fold>

    @NotNull public SecurityId getSecurityId()
    {
        return security.getSecurityId();
    }
}
