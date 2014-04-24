package com.tradehero.th.models.watchlist;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;
import retrofit.Callback;
import retrofit.client.Response;

abstract public class MiddleCallbackWatchlistChange extends MiddleCallbackWatchlistUpdate
{
    protected final UserBaseKey concernedUser;
    protected final UserWatchlistPositionCache userWatchlistPositionCache;
    protected final PortfolioCompactListCache portfolioCompactListCache;

    public MiddleCallbackWatchlistChange(
            Callback<WatchlistPositionDTO> primaryCallback,
            WatchlistPositionCache watchlistPositionCache,
            UserBaseKey concernedUser,
            UserWatchlistPositionCache userWatchlistPositionCache,
            PortfolioCompactListCache portfolioCompactListCache)
    {
        super(primaryCallback, watchlistPositionCache);
        this.concernedUser = concernedUser;
        this.userWatchlistPositionCache = userWatchlistPositionCache;
        this.portfolioCompactListCache = portfolioCompactListCache;
    }

    @Override public void success(WatchlistPositionDTO watchlistPositionDTO, Response response)
    {
        updateCache(watchlistPositionDTO);
        super.success(watchlistPositionDTO, response);
    }

    protected void updateCache(WatchlistPositionDTO watchlistPositionDTO)
    {
        if (portfolioCompactListCache != null)
        {
            portfolioCompactListCache.invalidate(concernedUser);
        }
    }
}
