package com.tradehero.th.models.security;

import android.support.annotation.NonNull;
import com.tradehero.th.api.position.SecurityPositionDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.models.ThroughDTOProcessor;

public class DTOProcessorSecurityPositionReceived<SecurityPositionDTOType extends SecurityPositionDTO>
        extends ThroughDTOProcessor<SecurityPositionDTOType>
{
    @NonNull protected final SecurityId securityId;
    @NonNull protected final UserBaseKey ownerId;

    //<editor-fold desc="Constructors">
    public DTOProcessorSecurityPositionReceived(
            @NonNull SecurityId securityId,
            @NonNull UserBaseKey ownerId)
    {
        this.securityId = securityId;
        this.ownerId = ownerId;
    }
    //</editor-fold>
}
