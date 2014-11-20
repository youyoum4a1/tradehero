package com.tradehero.th.models.watchlist;

import android.support.annotation.NonNull;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import com.tradehero.th.models.ThroughDTOProcessor;
import com.tradehero.th.persistence.portfolio.PortfolioCacheRx;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCacheRx;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCacheRx;
import timber.log.Timber;

public class DTOProcessorWatchlistDelete extends ThroughDTOProcessor<WatchlistPositionDTO>
{
    @NonNull private final UserBaseKey concernedUser;
    @NonNull private final WatchlistPositionCacheRx watchlistPositionCache;
    @NonNull private final PortfolioCacheRx portfolioCache;
    @NonNull private final UserWatchlistPositionCacheRx userWatchlistPositionCache;

    //<editor-fold desc="Constructors">
    public DTOProcessorWatchlistDelete(
            @NonNull WatchlistPositionCacheRx watchlistPositionCache,
            @NonNull UserBaseKey concernedUser,
            @NonNull PortfolioCacheRx portfolioCache,
            @NonNull UserWatchlistPositionCacheRx userWatchlistPositionCache)
    {
        super();
        this.concernedUser = concernedUser;
        this.watchlistPositionCache = watchlistPositionCache;
        this.portfolioCache = portfolioCache;
        this.userWatchlistPositionCache = userWatchlistPositionCache;
    }
    //</editor-fold>

    @Override public WatchlistPositionDTO process(@NonNull WatchlistPositionDTO watchlistPositionDTO)
    {
        portfolioCache.invalidate(concernedUser, true);
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

        WatchlistPositionDTOList cachedPositions = userWatchlistPositionCache.getValue(concernedUser);
        if (cachedPositions != null && deletedSecurityId != null)
        {
            cachedPositions.remove(deletedSecurityId);
            userWatchlistPositionCache.onNext(concernedUser, cachedPositions);
            watchlistPositionCache.invalidate(deletedSecurityId);
        }
        else
        {
            userWatchlistPositionCache.invalidate(concernedUser);
            watchlistPositionCache.invalidate(concernedUser);
        }
        return watchlistPositionDTO;
    }
}
