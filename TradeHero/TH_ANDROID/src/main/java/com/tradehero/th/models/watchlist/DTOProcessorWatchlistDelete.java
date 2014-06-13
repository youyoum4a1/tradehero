package com.tradehero.th.models.watchlist;

import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

public class DTOProcessorWatchlistDelete implements DTOProcessor<WatchlistPositionDTO>
{
    @NotNull private final UserBaseKey concernedUser;
    @NotNull private final WatchlistPositionCache watchlistPositionCache;
    @NotNull private final PortfolioCompactListCache portfolioCompactListCache;
    @NotNull private final UserWatchlistPositionCache userWatchlistPositionCache;

    public DTOProcessorWatchlistDelete(
            @NotNull WatchlistPositionCache watchlistPositionCache,
            @NotNull UserBaseKey concernedUser,
            @NotNull PortfolioCompactListCache portfolioCompactListCache,
            @NotNull UserWatchlistPositionCache userWatchlistPositionCache)
    {
        super();
        this.concernedUser = concernedUser;
        this.watchlistPositionCache = watchlistPositionCache;
        this.portfolioCompactListCache = portfolioCompactListCache;
        this.userWatchlistPositionCache = userWatchlistPositionCache;
    }

    @Override public WatchlistPositionDTO process(@NotNull WatchlistPositionDTO watchlistPositionDTO)
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
