package com.tradehero.th.models.security;

import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.models.DTOProcessor;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorSecurityPositionReceived implements DTOProcessor<SecurityPositionDetailDTO>
{
    @NotNull protected final SecurityId securityId;
    @NotNull protected final CurrentUserId currentUserId;

    //<editor-fold desc="Description">
    public DTOProcessorSecurityPositionReceived(
            @NotNull SecurityId securityId,
            @NotNull CurrentUserId currentUserId)
    {
        this.securityId = securityId;
        this.currentUserId = currentUserId;
    }
    //</editor-fold>

    @Override public SecurityPositionDetailDTO process(@NotNull SecurityPositionDetailDTO value)
    {
        if (value.portfolio != null)
        {
            value.portfolio.userId = currentUserId.get();
        }
        if (value.providers != null)
        {
            for (@NotNull ProviderDTO providerDTO : value.providers)
            {
                if (providerDTO.associatedPortfolio != null)
                {
                    providerDTO.associatedPortfolio.userId = currentUserId.get();
                }
            }
        }

        return value;
    }
}
