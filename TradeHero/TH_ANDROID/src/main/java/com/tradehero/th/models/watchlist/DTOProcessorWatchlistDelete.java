package com.tradehero.th.models.watchlist;

import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;
import timber.log.Timber;

public class DTOProcessorWatchlistDelete implements DTOProcessor<WatchlistPositionDTO>
{
    private final UserBaseKey concernedUser;
    private final WatchlistPositionCache watchlistPositionCache;
    private final PortfolioCompactListCache portfolioCompactListCache;
    private final UserWatchlistPositionCache userWatchlistPositionCache;

    public DTOProcessorWatchlistDelete(
            WatchlistPositionCache watchlistPositionCache,
            UserBaseKey concernedUser,
            PortfolioCompactListCache portfolioCompactListCache,
            UserWatchlistPositionCache userWatchlistPositionCache)
    {
        super();
        this.concernedUser = concernedUser;
        this.watchlistPositionCache = watchlistPositionCache;
        this.portfolioCompactListCache = portfolioCompactListCache;
        this.userWatchlistPositionCache = userWatchlistPositionCache;
    }

    @Override public WatchlistPositionDTO process(WatchlistPositionDTO watchlistPositionDTO)
    {
        portfolioCompactListCache.invalidate(concernedUser);
        SecurityId deletedSecurityId = null;
        if (watchlistPositionDTO.securityDTO != null)
        {
            deletedSecurityId = watchlistPositionDTO.securityDTO.getSecurityId();
        }
        else
        {
            Timber.e(
                    new NullPointerException("watchlist or security null " + watchlistPositionDTO),
                    null);
        }

        SecurityIdList currentIds = userWatchlistPositionCache.get(concernedUser);
        if (currentIds != null)
        {
            currentIds.remove(deletedSecurityId);
        }
        watchlistPositionCache.invalidate(deletedSecurityId);
        return watchlistPositionDTO;
    }
}
