package com.tradehero.th.models.security;

import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.UserBaseKey;
import android.support.annotation.NonNull;

public class DTOProcessorSecurityPositionDetailReceived
        extends DTOProcessorSecurityPositionReceived<SecurityPositionDetailDTO>
{
    //<editor-fold desc="Description">
    public DTOProcessorSecurityPositionDetailReceived(
            @NonNull SecurityId securityId,
            @NonNull UserBaseKey ownerId)
    {
        super(securityId, ownerId);
    }
    //</editor-fold>

    @Override public SecurityPositionDetailDTO process(@NonNull SecurityPositionDetailDTO value)
    {
        if (value.providers != null)
        {
            for (ProviderDTO providerDTO : value.providers)
            {
                if (providerDTO.associatedPortfolio != null)
                {
                    providerDTO.associatedPortfolio.userId = ownerId.key;
                }
            }
        }

        return value;
    }
}
