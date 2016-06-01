package com.ayondo.academy.models.security;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.position.SecurityPositionDTO;
import com.ayondo.academy.api.security.SecurityId;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.models.ThroughDTOProcessor;

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
