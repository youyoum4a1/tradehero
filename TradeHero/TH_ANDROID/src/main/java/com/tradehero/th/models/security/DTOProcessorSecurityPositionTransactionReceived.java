package com.tradehero.th.models.security;

import com.tradehero.th.api.position.SecurityPositionTransactionDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.position.SecurityPositionDetailCacheRx;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorSecurityPositionTransactionReceived extends DTOProcessorSecurityPositionReceived<SecurityPositionTransactionDTO>
{
    @NotNull private final SecurityPositionDetailCacheRx securityPositionDetailCache;

    //<editor-fold desc="Description">
    public DTOProcessorSecurityPositionTransactionReceived(
            @NotNull SecurityId securityId,
            @NotNull UserBaseKey ownerId,
            @NotNull SecurityPositionDetailCacheRx securityPositionDetailCache)
    {
        super(securityId, ownerId);
        this.securityPositionDetailCache = securityPositionDetailCache;
    }
    //</editor-fold>

    @Override public SecurityPositionTransactionDTO process(@NotNull SecurityPositionTransactionDTO value)
    {
        value = super.process(value);
        securityPositionDetailCache.invalidate(securityId);
        value.portfolio.userId = ownerId.key;
        return value;
    }
}
