package com.ayondo.academy.models.watchlist;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.api.watchlist.WatchlistPositionDTO;
import com.ayondo.academy.models.ThroughDTOProcessor;
import com.ayondo.academy.persistence.portfolio.PortfolioCacheRx;
import com.ayondo.academy.persistence.watchlist.WatchlistPositionCacheRx;
import timber.log.Timber;

public class DTOProcessorWatchlistUpdate extends ThroughDTOProcessor<WatchlistPositionDTO>
{
    @NonNull protected final UserBaseKey concernedUser;
    @NonNull protected final WatchlistPositionCacheRx watchlistPositionCache;
    @NonNull protected final PortfolioCacheRx portfolioCache;

    //<editor-fold desc="Constructors">
    public DTOProcessorWatchlistUpdate(
            @NonNull UserBaseKey concernedUser,
            @NonNull WatchlistPositionCacheRx watchlistPositionCache,
            @NonNull PortfolioCacheRx portfolioCache)
    {
        this.concernedUser = concernedUser;
        this.watchlistPositionCache = watchlistPositionCache;
        this.portfolioCache = portfolioCache;
    }
    //</editor-fold>

    @Nullable
    @Override public WatchlistPositionDTO process(@Nullable WatchlistPositionDTO watchlistPositionDTO)
    {
        portfolioCache.invalidate(concernedUser, true);
        if (watchlistPositionDTO != null && watchlistPositionDTO.securityDTO != null)
        {
            watchlistPositionCache.onNext(watchlistPositionDTO.securityDTO.getSecurityId(), watchlistPositionDTO);
        }
        else
        {
            watchlistPositionCache.invalidate(concernedUser);
            Timber.e(new NullPointerException("watchlist or security null " + watchlistPositionDTO), "");
        }
        return watchlistPositionDTO;
    }
}
