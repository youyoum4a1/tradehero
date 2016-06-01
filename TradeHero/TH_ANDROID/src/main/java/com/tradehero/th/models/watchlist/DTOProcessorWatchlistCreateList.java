package com.ayondo.academy.models.watchlist;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.api.watchlist.WatchlistPositionDTO;
import com.ayondo.academy.api.watchlist.WatchlistPositionDTOList;
import com.ayondo.academy.models.ThroughDTOProcessor;
import com.ayondo.academy.persistence.portfolio.PortfolioCacheRx;
import com.ayondo.academy.persistence.watchlist.UserWatchlistPositionCacheRx;
import com.ayondo.academy.persistence.watchlist.WatchlistPositionCacheRx;

public class DTOProcessorWatchlistCreateList extends ThroughDTOProcessor<WatchlistPositionDTOList>
{
    @NonNull DTOProcessorWatchlistCreate individualProcessor;

    //<editor-fold desc="Constructors">
    public DTOProcessorWatchlistCreateList(
            @NonNull WatchlistPositionCacheRx watchlistPositionCache,
            @NonNull UserBaseKey concernedUser,
            @NonNull PortfolioCacheRx portfolioCache,
            @NonNull UserWatchlistPositionCacheRx userWatchlistPositionCache)
    {
        super();
        individualProcessor = new DTOProcessorWatchlistCreate(
                watchlistPositionCache,
                concernedUser,
                portfolioCache,
                userWatchlistPositionCache);
    }
    //</editor-fold>

    @Override public WatchlistPositionDTOList process(@NonNull WatchlistPositionDTOList value)
    {
        WatchlistPositionDTOList processed = new WatchlistPositionDTOList();
        for (WatchlistPositionDTO position : value)
        {
            processed.add(individualProcessor.process(position));
        }
        return processed;
    }
}
