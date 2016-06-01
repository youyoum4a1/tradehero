package com.ayondo.academy.persistence.portfolio;

import android.support.annotation.NonNull;
import android.util.Pair;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.api.portfolio.OwnedPortfolioIdList;
import com.ayondo.academy.api.portfolio.PortfolioCompactDTO;
import com.ayondo.academy.api.portfolio.PortfolioCompactDTOList;
import com.ayondo.academy.api.portfolio.key.PortfolioCompactListKey;
import com.ayondo.academy.api.security.SecurityId;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.network.service.SecurityServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import rx.functions.Func1;

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
                    .map(new Func1<Pair<UserBaseKey, PortfolioCompactDTOList>, OwnedPortfolioIdList>()
                    {
                        @Override public OwnedPortfolioIdList call(Pair<UserBaseKey, PortfolioCompactDTOList> pair)
                        {
                            return new OwnedPortfolioIdList(pair.second, new PortfolioCompactDTO());
                        }
                    });
        }
        if (key instanceof SecurityId)
        {
            return securityServiceWrapper.get().getApplicablePortfolioIdsRx((SecurityId) key);
        }

        throw new IllegalArgumentException("Unhandled key " + key);
    }
}
