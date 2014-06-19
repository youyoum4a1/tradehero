package com.tradehero.th.models.watchlist;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DTOProcessorWatchlistChange extends DTOProcessorWatchlistUpdate
{
    @NotNull private final UserBaseKey concernedUser;
    @NotNull private final PortfolioCompactListCache portfolioCompactListCache;

    public DTOProcessorWatchlistChange(
            @NotNull WatchlistPositionCache watchlistPositionCache,
            @NotNull UserBaseKey concernedUser,
            @NotNull PortfolioCompactListCache portfolioCompactListCache)
    {
        super(watchlistPositionCache);
        this.concernedUser = concernedUser;
        this.portfolioCompactListCache = portfolioCompactListCache;
    }

    @Nullable
    @Override public WatchlistPositionDTO process(@Nullable WatchlistPositionDTO watchlistPositionDTO)
    {
        @Nullable WatchlistPositionDTO processed = super.process(watchlistPositionDTO);
        portfolioCompactListCache.invalidate(concernedUser);
        return processed;
    }
}
