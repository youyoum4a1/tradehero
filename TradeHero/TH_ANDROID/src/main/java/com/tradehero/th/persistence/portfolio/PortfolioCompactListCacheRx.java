package com.tradehero.th.persistence.portfolio;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.PortfolioServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class PortfolioCompactListCacheRx extends BaseFetchDTOCacheRx<UserBaseKey, PortfolioCompactDTOList>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 50;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 5;

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
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
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
