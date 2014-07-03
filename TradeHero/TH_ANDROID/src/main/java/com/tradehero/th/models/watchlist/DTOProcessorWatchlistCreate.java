package com.tradehero.th.models.watchlist;

import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DTOProcessorWatchlistCreate extends DTOProcessorWatchlistChange
{
    @NotNull private final UserBaseKey concernedUser;
    @NotNull private final UserWatchlistPositionCache userWatchlistPositionCache;

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

    @Nullable
    @Override public WatchlistPositionDTO process(@Nullable WatchlistPositionDTO watchlistPositionDTO)
    {
        @Nullable WatchlistPositionDTO processed = super.process(watchlistPositionDTO);
        @Nullable SecurityIdList currentIds = userWatchlistPositionCache.get(concernedUser);
        if (currentIds != null)
        {
            // Remove this test when #70827276 is fixed
            if (processed != null && processed.securityDTO != null)
            {
                currentIds.add(0, processed.securityDTO.getSecurityId());
            }
        }
        return processed;
    }
}
