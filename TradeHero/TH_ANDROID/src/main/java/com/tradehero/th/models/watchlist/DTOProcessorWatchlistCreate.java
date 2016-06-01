package com.ayondo.academy.models.watchlist;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.api.watchlist.WatchlistPositionDTO;
import com.ayondo.academy.api.watchlist.WatchlistPositionDTOList;
import com.ayondo.academy.persistence.portfolio.PortfolioCacheRx;
import com.ayondo.academy.persistence.watchlist.UserWatchlistPositionCacheRx;
import com.ayondo.academy.persistence.watchlist.WatchlistPositionCacheRx;

public class DTOProcessorWatchlistCreate extends DTOProcessorWatchlistUpdate
{
    @NonNull protected final UserWatchlistPositionCacheRx userWatchlistPositionCache;

    //<editor-fold desc="Constructors">
    public DTOProcessorWatchlistCreate(
            @NonNull WatchlistPositionCacheRx watchlistPositionCache,
            @NonNull UserBaseKey concernedUser,
            @NonNull PortfolioCacheRx portfolioCache,
            @NonNull UserWatchlistPositionCacheRx userWatchlistPositionCache)
    {
        super(concernedUser,watchlistPositionCache, portfolioCache);
        this.userWatchlistPositionCache = userWatchlistPositionCache;
    }
    //</editor-fold>

    @Nullable
    @Override public WatchlistPositionDTO process(@Nullable WatchlistPositionDTO watchlistPositionDTO)
    {
        WatchlistPositionDTO processed = super.process(watchlistPositionDTO);
        WatchlistPositionDTOList cached = userWatchlistPositionCache.getCachedValue(concernedUser);
        if (cached != null)
        {
            // Remove this test when #70827276 is fixed
            if (processed != null && processed.securityDTO != null)
            {
                cached.add(0, processed);
                userWatchlistPositionCache.onNext(concernedUser, cached);
            }
        }
        return processed;
    }
}
