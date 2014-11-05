package com.tradehero.th.models.security;

import com.tradehero.th.api.position.SecurityPositionTransactionDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.position.SecurityPositionDetailCacheRx;
import android.support.annotation.NonNull;

public class DTOProcessorSecurityPositionTransactionReceived extends DTOProcessorSecurityPositionReceived<SecurityPositionTransactionDTO>
{
    @NonNull private final SecurityPositionDetailCacheRx securityPositionDetailCache;

    //<editor-fold desc="Description">
    public DTOProcessorSecurityPositionTransactionReceived(
            @NonNull SecurityId securityId,
            @NonNull UserBaseKey ownerId,
            @NonNull SecurityPositionDetailCacheRx securityPositionDetailCache)
    {
        super(securityId, ownerId);
        this.securityPositionDetailCache = securityPositionDetailCache;
    }
    //</editor-fold>

    @Override public SecurityPositionTransactionDTO process(@NonNull SecurityPositionTransactionDTO value)
    {
        value = super.process(value);
        securityPositionDetailCache.invalidate(securityId);
        value.portfolio.userId = ownerId.key;
        return value;
    }
}
