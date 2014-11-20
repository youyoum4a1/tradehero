package com.tradehero.th.models.security;

import android.support.annotation.NonNull;
import com.tradehero.th.api.position.SecurityPositionTransactionDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.UserBaseKey;

public class DTOProcessorSecurityPositionTransactionReceived extends DTOProcessorSecurityPositionReceived<SecurityPositionTransactionDTO>
{
    //<editor-fold desc="Description">
    public DTOProcessorSecurityPositionTransactionReceived(
            @NonNull SecurityId securityId,
            @NonNull UserBaseKey ownerId)
    {
        super(securityId, ownerId);
    }
    //</editor-fold>

    @Override public SecurityPositionTransactionDTO process(@NonNull SecurityPositionTransactionDTO value)
    {
        value = super.process(value);
        value.portfolio.userId = ownerId.key;
        return value;
    }
}
