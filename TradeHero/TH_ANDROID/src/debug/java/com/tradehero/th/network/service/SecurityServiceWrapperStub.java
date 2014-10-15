package com.tradehero.th.network.service;

import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.key.ExchangeSectorSecurityListType;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.api.security.key.TrendingBasicSecurityListType;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.persistence.position.SecurityPositionDetailCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class SecurityServiceWrapperStub extends SecurityServiceWrapper
{
    //<editor-fold desc="Constructors">
    @Inject public SecurityServiceWrapperStub(
            @NotNull SecurityService securityService,
            @NotNull SecurityServiceAsync securityServiceAsync,
            @NotNull SecurityServiceRx securityServiceRx,
            @NotNull ProviderServiceWrapper providerServiceWrapper,
            @NotNull SecurityPositionDetailCache securityPositionDetailCache,
            @NotNull SecurityCompactCache securityCompactCache,
            @NotNull UserProfileCache userProfileCache,
            @NotNull CurrentUserId currentUserId)
    {
        super(securityService, securityServiceAsync, securityServiceRx, providerServiceWrapper,
                securityPositionDetailCache, securityCompactCache, userProfileCache,
                currentUserId);
    }
    //</editor-fold>

    @Override public SecurityCompactDTOList getSecurities(@NotNull SecurityListType key)
    {
        if (key instanceof ExchangeSectorSecurityListType)
        {
            key = new TrendingBasicSecurityListType();
        }
        return super.getSecurities(key);
    }
}
