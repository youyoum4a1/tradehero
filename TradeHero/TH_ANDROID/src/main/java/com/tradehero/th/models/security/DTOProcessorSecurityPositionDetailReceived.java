package com.tradehero.th.models.security;

import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.UserBaseKey;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorSecurityPositionDetailReceived
        extends DTOProcessorSecurityPositionReceived<SecurityPositionDetailDTO>
{
    //<editor-fold desc="Description">
    public DTOProcessorSecurityPositionDetailReceived(
            @NotNull SecurityId securityId,
            @NotNull UserBaseKey ownerId)
    {
        super(securityId, ownerId);
    }
    //</editor-fold>

    @Override public SecurityPositionDetailDTO process(@NotNull SecurityPositionDetailDTO value)
    {
        for (@NotNull ProviderDTO providerDTO : value.providers)
        {
            if (providerDTO.associatedPortfolio != null)
            {
                providerDTO.associatedPortfolio.userId = ownerId.key;
            }
        }

        return value;
    }
}
