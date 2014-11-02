package com.tradehero.th.models.watchlist;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.portfolio.PortfolioCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCacheRx;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorWatchlistCreateList implements DTOProcessor<WatchlistPositionDTOList>
{
    @NotNull DTOProcessorWatchlistCreate individualProcessor;

    //<editor-fold desc="Constructors">
    public DTOProcessorWatchlistCreateList(
            @NotNull WatchlistPositionCache watchlistPositionCache,
            @NotNull UserBaseKey concernedUser,
            @NotNull PortfolioCompactCacheRx portfolioCompactCache,
            @NotNull PortfolioCacheRx portfolioCache,
            @NotNull UserWatchlistPositionCache userWatchlistPositionCache)
    {
        super();
        individualProcessor = new DTOProcessorWatchlistCreate(
                watchlistPositionCache,
                concernedUser,
                portfolioCompactCache,
                portfolioCache,
                userWatchlistPositionCache);
    }
    //</editor-fold>

    @Override public WatchlistPositionDTOList process(@NotNull WatchlistPositionDTOList value)
    {
        WatchlistPositionDTOList processed = new WatchlistPositionDTOList();
        for (WatchlistPositionDTO position : value)
        {
            processed.add(individualProcessor.process(position));
        }
        return processed;
    }
}
