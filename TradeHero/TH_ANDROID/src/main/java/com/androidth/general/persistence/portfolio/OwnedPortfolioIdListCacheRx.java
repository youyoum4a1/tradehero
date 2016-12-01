package com.androidth.general.persistence.portfolio;

import android.support.annotation.NonNull;
import android.util.Pair;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.api.portfolio.OwnedPortfolioIdList;
import com.androidth.general.api.portfolio.PortfolioCompactDTO;
import com.androidth.general.api.portfolio.PortfolioCompactDTOList;
import com.androidth.general.api.portfolio.key.PortfolioCompactListKey;
import com.androidth.general.api.security.SecurityId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.network.service.SecurityServiceWrapper;
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
            return securityServiceWrapper.get().getApplicablePortfolioIdsRxMainThread((SecurityId) key);
        }

        throw new IllegalArgumentException("Unhandled key " + key);
    }
}
