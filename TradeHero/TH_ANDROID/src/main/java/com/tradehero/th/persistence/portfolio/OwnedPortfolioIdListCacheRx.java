package com.tradehero.th.persistence.portfolio;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.portfolio.OwnedPortfolioIdList;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.key.PortfolioCompactListKey;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class OwnedPortfolioIdListCacheRx extends BaseFetchDTOCacheRx<PortfolioCompactListKey, OwnedPortfolioIdList>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 50;

    @NonNull protected final Lazy<SecurityServiceWrapper> securityServiceWrapper;
    @NonNull protected final Lazy<PortfolioCompactListCacheRx> portfolioCompactListCacheRx;

    //<editor-fold desc="Constructors">
    @Inject protected OwnedPortfolioIdListCacheRx(
            @NonNull Lazy<SecurityServiceWrapper> securityServiceWrapper,
            @NonNull Lazy<PortfolioCompactListCacheRx> portfolioCompactListCacheRx,
            @NonNull DTOCacheUtilRx dtoCacheUtilRx)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtilRx);
        this.securityServiceWrapper = securityServiceWrapper;
        this.portfolioCompactListCacheRx = portfolioCompactListCacheRx;
    }
    //</editor-fold>

    @NonNull @Override protected Observable<OwnedPortfolioIdList> fetch(@NonNull PortfolioCompactListKey key)
    {
        if (key instanceof UserBaseKey)
        {
            return portfolioCompactListCacheRx.get().get((UserBaseKey) key)
                    .map(pair -> new OwnedPortfolioIdList(pair.second, new PortfolioCompactDTO()));
        }
        if (key instanceof SecurityId)
        {
            return securityServiceWrapper.get().getApplicablePortfolioIdsRx((SecurityId) key);
        }

        throw new IllegalArgumentException("Unhandled key " + key);
    }
}
