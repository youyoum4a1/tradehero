package com.tradehero.th.models.watchlist;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class DTOProcessorWatchlistUpdate implements DTOProcessor<WatchlistPositionDTO>
{
    @NotNull protected final UserBaseKey concernedUser;
    @NotNull protected final WatchlistPositionCache watchlistPositionCache;
    @NotNull protected final PortfolioCompactCache portfolioCompactCache;
    @NotNull protected final PortfolioCache portfolioCache;

    //<editor-fold desc="Constructors">
    public DTOProcessorWatchlistUpdate(
            @NotNull UserBaseKey concernedUser,
            @NotNull WatchlistPositionCache watchlistPositionCache,
            @NotNull PortfolioCompactCache portfolioCompactCache,
            @NotNull PortfolioCache portfolioCache)
    {
        this.concernedUser = concernedUser;
        this.watchlistPositionCache = watchlistPositionCache;
        this.portfolioCompactCache = portfolioCompactCache;
        this.portfolioCache = portfolioCache;
    }
    //</editor-fold>

    @Nullable
    @Override public WatchlistPositionDTO process(@Nullable WatchlistPositionDTO watchlistPositionDTO)
    {
        portfolioCompactCache.invalidate(concernedUser, true);
        portfolioCache.invalidate(concernedUser, true);
        if (watchlistPositionDTO != null && watchlistPositionDTO.securityDTO != null)
        {
            watchlistPositionCache.put(watchlistPositionDTO.securityDTO.getSecurityId(), watchlistPositionDTO);
        }
        else
        {
            watchlistPositionCache.invalidate(concernedUser);
            Timber.e(new NullPointerException("watchlist or security null " + watchlistPositionDTO), "");
        }
        return watchlistPositionDTO;
    }
}
