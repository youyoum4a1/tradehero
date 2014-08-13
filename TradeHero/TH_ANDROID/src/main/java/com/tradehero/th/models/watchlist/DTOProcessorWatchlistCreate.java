package com.tradehero.th.models.watchlist;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DTOProcessorWatchlistCreate extends DTOProcessorWatchlistChange
{
    @NotNull private final UserBaseKey concernedUser;
    @NotNull private final UserWatchlistPositionCache userWatchlistPositionCache;

    //<editor-fold desc="Constructors">
    public DTOProcessorWatchlistCreate(
            @NotNull WatchlistPositionCache watchlistPositionCache,
            @NotNull UserBaseKey concernedUser,
            @NotNull PortfolioCompactListCache portfolioCompactListCache,
            @NotNull UserWatchlistPositionCache userWatchlistPositionCache)
    {
        super(watchlistPositionCache, concernedUser, portfolioCompactListCache);
        this.concernedUser = concernedUser;
        this.userWatchlistPositionCache = userWatchlistPositionCache;
    }
    //</editor-fold>

    @Nullable
    @Override public WatchlistPositionDTO process(@Nullable WatchlistPositionDTO watchlistPositionDTO)
    {
        @Nullable WatchlistPositionDTO processed = super.process(watchlistPositionDTO);
        @Nullable WatchlistPositionDTOList cached = userWatchlistPositionCache.get(concernedUser);
        if (cached != null)
        {
            // Remove this test when #70827276 is fixed
            if (processed != null && processed.securityDTO != null)
            {
                cached.add(0, processed);
                userWatchlistPositionCache.put(concernedUser, cached);
            }
        }
        return processed;
    }
}
