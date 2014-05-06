package com.tradehero.th.models.watchlist;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;

public class DTOProcessorWatchlistChange extends DTOProcessorWatchlistUpdate
{
    private final UserBaseKey concernedUser;
    private final PortfolioCompactListCache portfolioCompactListCache;

    public DTOProcessorWatchlistChange(
            WatchlistPositionCache watchlistPositionCache,
            UserBaseKey concernedUser,
            PortfolioCompactListCache portfolioCompactListCache)
    {
        super(watchlistPositionCache);
        this.concernedUser = concernedUser;
        this.portfolioCompactListCache = portfolioCompactListCache;
    }

    @Override public WatchlistPositionDTO process(WatchlistPositionDTO watchlistPositionDTO)
    {
        WatchlistPositionDTO processed = super.process(watchlistPositionDTO);
        portfolioCompactListCache.invalidate(concernedUser);
        return processed;
    }
}
