package com.tradehero.th.models.watchlist;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import com.tradehero.th.persistence.portfolio.PortfolioCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCacheRx;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class DTOProcessorWatchlistCreate extends DTOProcessorWatchlistUpdate
{
    @NonNull protected final UserWatchlistPositionCache userWatchlistPositionCache;

    //<editor-fold desc="Constructors">
    public DTOProcessorWatchlistCreate(
            @NonNull WatchlistPositionCache watchlistPositionCache,
            @NonNull UserBaseKey concernedUser,
            @NonNull PortfolioCompactCacheRx portfolioCompactCache,
            @NonNull PortfolioCacheRx portfolioCache,
            @NonNull UserWatchlistPositionCache userWatchlistPositionCache)
    {
        super(concernedUser,watchlistPositionCache, portfolioCompactCache, portfolioCache);
        this.userWatchlistPositionCache = userWatchlistPositionCache;
    }
    //</editor-fold>

    @Nullable
    @Override public WatchlistPositionDTO process(@Nullable WatchlistPositionDTO watchlistPositionDTO)
    {
        WatchlistPositionDTO processed = super.process(watchlistPositionDTO);
        WatchlistPositionDTOList cached = userWatchlistPositionCache.get(concernedUser);
        if (cached != null)
        {
            // Remove this test when #70827276 is fixed
            if (processed != null && processed.securityDTO != null)
            {
                cached.add(0, processed);
                userWatchlistPositionCache.put(concernedUser, cached);
            }
        }
        return processed;
    }
}
