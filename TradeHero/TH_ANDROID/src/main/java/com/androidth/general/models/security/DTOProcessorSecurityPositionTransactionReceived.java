package com.androidth.general.models.security;

import android.support.annotation.NonNull;
import com.androidth.general.api.position.SecurityPositionTransactionDTO;
import com.androidth.general.api.security.SecurityId;
import com.androidth.general.api.users.UserBaseKey;

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
