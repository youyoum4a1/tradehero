package com.tradehero.th.persistence.portfolio;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.PortfolioServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class PortfolioCacheRx extends BaseFetchDTOCacheRx<OwnedPortfolioId, PortfolioDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 200;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 20;

    @NonNull protected final Lazy<PortfolioServiceWrapper> portfolioServiceWrapper;
    @NonNull protected final Lazy<PortfolioCompactCacheRx> portfolioCompactCache;

    //<editor-fold desc="Constructors">
    @Inject public PortfolioCacheRx(
            @NonNull Lazy<PortfolioServiceWrapper> portfolioServiceWrapper,
            @NonNull Lazy<PortfolioCompactCacheRx> portfolioCompactCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
        this.portfolioServiceWrapper = portfolioServiceWrapper;
        this.portfolioCompactCache = portfolioCompactCache;
    }
    //</editor-fold>

    @Override @NonNull protected Observable<PortfolioDTO> fetch(@NonNull OwnedPortfolioId key)
    {
        return portfolioServiceWrapper.get().getPortfolioRx(key);
    }

    @Override public void onNext(@NonNull OwnedPortfolioId key, @NonNull PortfolioDTO value)
    {
        PortfolioDTO previous = getValue(key);
        //noinspection ConstantConditions
        if (previous != null && previous.userId != null)
        {
            value.userId = previous.userId;
        }
        //noinspection ConstantConditions
        if (value.userId == null)
        {
            throw new NullPointerException("UserId should be set");
        }
        portfolioCompactCache.get().onNext(key.getPortfolioIdKey(), value);
        super.onNext(key, value);
    }

    public void invalidate(@NonNull UserBaseKey concernedUser)
    {
        invalidate(concernedUser, false);
    }

    public void invalidate(@NonNull UserBaseKey concernedUser, boolean onlyWatchlist)
    {
        PortfolioDTO cached;
        for (OwnedPortfolioId key : snapshot().keySet())
        {
            cached = getValue(key);
            if (cached != null
                    && key.userId.equals(concernedUser.key)
                    && (cached.isWatchlist || !onlyWatchlist))
            {
                invalidate(key);
            }
        }
    }
}
