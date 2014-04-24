package com.tradehero.th.models.watchlist;

import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;
import retrofit.Callback;

public class MiddleCallbackWatchlistCreate extends MiddleCallbackWatchlistChange
{
    public MiddleCallbackWatchlistCreate(
            Callback<WatchlistPositionDTO> primaryCallback,
            WatchlistPositionCache watchlistPositionCache,
            UserBaseKey concernedUser,
            UserWatchlistPositionCache userWatchlistPositionCache,
            PortfolioCompactListCache portfolioCompactListCache)
    {
        super(
                primaryCallback,
                watchlistPositionCache,
                concernedUser,
                userWatchlistPositionCache,
                portfolioCompactListCache);
    }

    protected void updateCache(WatchlistPositionDTO watchlistPositionDTO)
    {
        super.updateCache(watchlistPositionDTO);
        if (userWatchlistPositionCache != null)
        {
            SecurityIdList currentIds = userWatchlistPositionCache.get(concernedUser);
            if (currentIds != null)
            {
                currentIds.add(0, watchlistPositionDTO.securityDTO.getSecurityId());
            }
        }
    }
}
