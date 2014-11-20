package com.tradehero.th.models.watchlist;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import com.tradehero.th.persistence.portfolio.PortfolioCacheRx;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCacheRx;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCacheRx;

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
        WatchlistPositionDTOList cached = userWatchlistPositionCache.getValue(concernedUser);
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
