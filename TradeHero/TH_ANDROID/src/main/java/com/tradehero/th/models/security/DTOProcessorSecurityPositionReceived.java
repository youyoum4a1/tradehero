package com.tradehero.th.models.security;

import com.tradehero.th.api.position.SecurityPositionDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.models.ThroughDTOProcessor;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorSecurityPositionReceived<SecurityPositionDTOType extends SecurityPositionDTO>
        extends ThroughDTOProcessor<SecurityPositionDTOType>
{
    @NotNull protected final SecurityId securityId;
    @NotNull protected final UserBaseKey ownerId;

    //<editor-fold desc="Constructors">
    public DTOProcessorSecurityPositionReceived(
            @NotNull SecurityId securityId,
            @NotNull UserBaseKey ownerId)
    {
        this.securityId = securityId;
        this.ownerId = ownerId;
    }
    //</editor-fold>
}
