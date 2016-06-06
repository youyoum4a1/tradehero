package com.androidth.general.models.security;

import android.support.annotation.NonNull;
import com.androidth.general.api.position.SecurityPositionDTO;
import com.androidth.general.api.security.SecurityId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.models.ThroughDTOProcessor;

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
