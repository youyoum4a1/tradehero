package com.androidth.general.persistence.portfolio;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.api.portfolio.PortfolioCompactDTOList;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.network.service.PortfolioServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class PortfolioCompactListCacheRx extends BaseFetchDTOCacheRx<UserBaseKey, PortfolioCompactDTOList>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 50;

    @NonNull protected final Lazy<PortfolioServiceWrapper> portfolioServiceWrapper;
    @NonNull protected final Lazy<PortfolioCompactCacheRx> portfolioCompactCache;
    @NonNull protected final CurrentUserId currentUserId;

    //<editor-fold desc="Constructors">
    @Inject public PortfolioCompactListCacheRx(
            @NonNull Lazy<PortfolioServiceWrapper> portfolioServiceWrapper,
            @NonNull Lazy<PortfolioCompactCacheRx> portfolioCompactCache,
            @NonNull CurrentUserId currentUserId,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.portfolioServiceWrapper = portfolioServiceWrapper;
        this.portfolioCompactCache = portfolioCompactCache;
        this.currentUserId = currentUserId;
    }
    //</editor-fold>

    @Override @NonNull protected Observable<PortfolioCompactDTOList> fetch(@NonNull UserBaseKey key)
    {
        return portfolioServiceWrapper.get().getPortfoliosRx(key, key.equals(currentUserId.toUserBaseKey()));
    }

    @Override public void onNext(@NonNull UserBaseKey key, @NonNull PortfolioCompactDTOList value)
    {
        portfolioCompactCache.get().onNext(value);
        super.onNext(key, value);
    }
}
