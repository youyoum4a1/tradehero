package com.tradehero.th.network.service;

import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.key.ExchangeSectorSecurityListType;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.api.security.key.TrendingBasicSecurityListType;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.persistence.portfolio.PortfolioCacheRx;
import com.tradehero.th.persistence.position.SecurityPositionDetailCacheRx;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;

@Singleton public class SecurityServiceWrapperStub extends SecurityServiceWrapper
{
    //<editor-fold desc="Constructors">
    @Inject public SecurityServiceWrapperStub(
            @NonNull SecurityService securityService,
            @NonNull SecurityServiceAsync securityServiceAsync,
            @NonNull SecurityServiceRx securityServiceRx,
            @NonNull ProviderServiceWrapper providerServiceWrapper,
            @NonNull Lazy<SecurityCompactCacheRx> securityCompactCache,
            @NonNull Lazy<SecurityPositionDetailCacheRx> securityPositionDetailCache,
            @NonNull Lazy<PortfolioCacheRx> portfolioCache,
            @NonNull CurrentUserId currentUserId)
    {
        super(securityService, securityServiceAsync, securityServiceRx, providerServiceWrapper,
                securityCompactCache, securityPositionDetailCache, portfolioCache,
                currentUserId);
    }
    //</editor-fold>

    @Override public SecurityCompactDTOList getSecurities(@NonNull SecurityListType key)
    {
        if (key instanceof ExchangeSectorSecurityListType)
        {
            key = new TrendingBasicSecurityListType();
        }
        return super.getSecurities(key);
    }
}
