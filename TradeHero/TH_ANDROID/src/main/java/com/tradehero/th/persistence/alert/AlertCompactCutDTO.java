package com.tradehero.th.persistence.alert;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import java.util.Date;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class AlertCompactCutDTO implements DTO
{
    public final int id;
    public final double targetPrice;
    public final Boolean upOrDown;
    public final Double priceMovement;
    public final boolean active;
    public final Date activeUntilDate;
    @Nullable public final SecurityId securityId;

    public AlertCompactCutDTO(
            @NotNull AlertCompactDTO alertCompactDTO,
            @NotNull SecurityCompactCache securityCompactCache)
    {
        if (alertCompactDTO.security != null)
        {
            securityCompactCache.put(alertCompactDTO.security.getSecurityId(), alertCompactDTO.security);
            this.securityId = alertCompactDTO.security.getSecurityId();
        }
        else
        {
            this.securityId = null;
        }
        this.id = alertCompactDTO.id;
        this.targetPrice = alertCompactDTO.targetPrice;
        this.upOrDown = alertCompactDTO.upOrDown;
        this.priceMovement = alertCompactDTO.priceMovement;
        this.active = alertCompactDTO.active;
        this.activeUntilDate = alertCompactDTO.activeUntilDate;
    }

    public AlertCompactDTO create(@NotNull SecurityCompactCache securityCompactCache)
    {
        AlertCompactDTO compactDTO = new AlertCompactDTO();
        compactDTO.id = this.id;
        compactDTO.targetPrice = this.targetPrice;
        compactDTO.upOrDown = this.upOrDown;
        compactDTO.priceMovement = this.priceMovement;
        compactDTO.active = this.active;
        compactDTO.activeUntilDate = this.activeUntilDate;
        if (securityId != null)
        {
            compactDTO.security = securityCompactCache.get(securityId);
        }
        else
        {
            compactDTO.security = null;
        }

        return compactDTO;
    }
}
