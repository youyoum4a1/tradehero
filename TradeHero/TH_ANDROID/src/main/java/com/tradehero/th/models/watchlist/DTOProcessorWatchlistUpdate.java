package com.tradehero.th.models.watchlist;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.portfolio.PortfolioCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCacheRx;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import rx.functions.Action1;
import timber.log.Timber;

public class DTOProcessorWatchlistUpdate implements DTOProcessor<WatchlistPositionDTO>,
        Action1<WatchlistPositionDTO>
{
    @NonNull protected final UserBaseKey concernedUser;
    @NonNull protected final WatchlistPositionCache watchlistPositionCache;
    @NonNull protected final PortfolioCompactCacheRx portfolioCompactCache;
    @NonNull protected final PortfolioCacheRx portfolioCache;

    //<editor-fold desc="Constructors">
    public DTOProcessorWatchlistUpdate(
            @NonNull UserBaseKey concernedUser,
            @NonNull WatchlistPositionCache watchlistPositionCache,
            @NonNull PortfolioCompactCacheRx portfolioCompactCache,
            @NonNull PortfolioCacheRx portfolioCache)
    {
        this.concernedUser = concernedUser;
        this.watchlistPositionCache = watchlistPositionCache;
        this.portfolioCompactCache = portfolioCompactCache;
        this.portfolioCache = portfolioCache;
    }
    //</editor-fold>

    @Nullable
    @Override public WatchlistPositionDTO process(@Nullable WatchlistPositionDTO watchlistPositionDTO)
    {
        portfolioCompactCache.invalidate(concernedUser, true);
        portfolioCache.invalidate(concernedUser, true);
        if (watchlistPositionDTO != null && watchlistPositionDTO.securityDTO != null)
        {
            watchlistPositionCache.put(watchlistPositionDTO.securityDTO.getSecurityId(), watchlistPositionDTO);
        }
        else
        {
            watchlistPositionCache.invalidate(concernedUser);
            Timber.e(new NullPointerException("watchlist or security null " + watchlistPositionDTO), "");
        }
        return watchlistPositionDTO;
    }

    @Override public void call(WatchlistPositionDTO watchlistPositionDTO)
    {
        process(watchlistPositionDTO);
    }
}
