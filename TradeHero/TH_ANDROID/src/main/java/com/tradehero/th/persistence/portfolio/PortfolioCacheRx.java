package com.tradehero.th.persistence.portfolio;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.PortfolioServiceWrapper;
import com.tradehero.th.persistence.position.GetPositionsCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rx.Observable;

@Singleton @UserCache
public class PortfolioCacheRx extends BaseFetchDTOCacheRx<OwnedPortfolioId, PortfolioDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 200;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 20;

    @NotNull protected final Lazy<PortfolioServiceWrapper> portfolioServiceWrapper;
    @NotNull protected final Lazy<PortfolioCompactCacheRx> portfolioCompactCache;
    @NotNull protected final PortfolioCompactListCacheRx portfolioCompactListCache;
    @NotNull protected final Lazy<GetPositionsCacheRx> getPositionsCache;

    //<editor-fold desc="Constructors">
    @Inject public PortfolioCacheRx(
            @NotNull Lazy<PortfolioServiceWrapper> portfolioServiceWrapper,
            @NotNull Lazy<PortfolioCompactCacheRx> portfolioCompactCache,
            @NotNull PortfolioCompactListCacheRx portfolioCompactListCache,
            @NotNull Lazy<GetPositionsCacheRx> getPositionsCache,
            @NotNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
        this.portfolioServiceWrapper = portfolioServiceWrapper;
        this.portfolioCompactCache = portfolioCompactCache;
        this.portfolioCompactListCache = portfolioCompactListCache;
        this.getPositionsCache = getPositionsCache;
    }
    //</editor-fold>

    @Override @NotNull protected Observable<PortfolioDTO> fetch(@NotNull OwnedPortfolioId key)
    {
        return portfolioServiceWrapper.get().getPortfolioRx(key);
    }

    @Override public void onNext(@NotNull OwnedPortfolioId key, @NotNull PortfolioDTO value)
    {
        @Nullable PortfolioDTO previous = getValue(key);
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
        getPositionsCache.get().invalidate(key);
        super.onNext(key, value);
    }

    public void invalidate(@NotNull UserBaseKey concernedUser)
    {
        invalidate(concernedUser, false);
    }

    public void invalidate(@NotNull UserBaseKey concernedUser, boolean onlyWatchlist)
    {
        PortfolioDTO cached;
        for (@NotNull OwnedPortfolioId key : snapshot().keySet())
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

    @Override public void invalidate(@NotNull OwnedPortfolioId key)
    {
        super.invalidate(key);
        getPositionsCache.get().invalidate(key);
        portfolioCompactListCache.get(key.getUserBaseKey());
    }
}
