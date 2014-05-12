package com.tradehero.th.models.watchlist;

import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;

public class DTOProcessorWatchlistCreate extends DTOProcessorWatchlistChange
{
    private final UserBaseKey concernedUser;
    private final UserWatchlistPositionCache userWatchlistPositionCache;

    public DTOProcessorWatchlistCreate(
            WatchlistPositionCache watchlistPositionCache,
            UserBaseKey concernedUser,
            PortfolioCompactListCache portfolioCompactListCache,
            UserWatchlistPositionCache userWatchlistPositionCache)
    {
        super(watchlistPositionCache, concernedUser, portfolioCompactListCache);
        this.concernedUser = concernedUser;
        this.userWatchlistPositionCache = userWatchlistPositionCache;
    }

    @Override public WatchlistPositionDTO process(WatchlistPositionDTO watchlistPositionDTO)
    {
        WatchlistPositionDTO processed = super.process(watchlistPositionDTO);
        SecurityIdList currentIds = userWatchlistPositionCache.get(concernedUser);
        if (currentIds != null)
        {
            // Remove this test when #70827276 is fixed
            if (watchlistPositionDTO.securityDTO != null)
            {
                currentIds.add(0, watchlistPositionDTO.securityDTO.getSecurityId());
            }
        }
        return processed;
    }
}
