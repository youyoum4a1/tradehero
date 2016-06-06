package com.androidth.general.models.watchlist;

import android.support.annotation.NonNull;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.watchlist.WatchlistPositionDTO;
import com.androidth.general.api.watchlist.WatchlistPositionDTOList;
import com.androidth.general.models.ThroughDTOProcessor;
import com.androidth.general.persistence.portfolio.PortfolioCacheRx;
import com.androidth.general.persistence.watchlist.UserWatchlistPositionCacheRx;
import com.androidth.general.persistence.watchlist.WatchlistPositionCacheRx;

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
