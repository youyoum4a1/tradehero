package com.androidth.general.models.watchlist;

import android.support.annotation.NonNull;
import com.androidth.general.api.security.SecurityId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.watchlist.WatchlistPositionDTO;
import com.androidth.general.api.watchlist.WatchlistPositionDTOList;
import com.androidth.general.models.ThroughDTOProcessor;
import com.androidth.general.persistence.portfolio.PortfolioCacheRx;
import com.androidth.general.persistence.watchlist.UserWatchlistPositionCacheRx;
import com.androidth.general.persistence.watchlist.WatchlistPositionCacheRx;
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

        WatchlistPositionDTOList cachedPositions = userWatchlistPositionCache.getCachedValue(concernedUser);
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