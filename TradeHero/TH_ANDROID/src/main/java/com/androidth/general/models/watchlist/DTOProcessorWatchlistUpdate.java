package com.androidth.general.models.watchlist;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.watchlist.WatchlistPositionDTO;
import com.androidth.general.models.ThroughDTOProcessor;
import com.androidth.general.persistence.portfolio.PortfolioCacheRx;
import com.androidth.general.persistence.watchlist.WatchlistPositionCacheRx;
import timber.log.Timber;

public class DTOProcessorWatchlistUpdate extends ThroughDTOProcessor<WatchlistPositionDTO>
{
    @NonNull protected final UserBaseKey concernedUser;
    @NonNull protected final WatchlistPositionCacheRx watchlistPositionCache;
    @NonNull protected final PortfolioCacheRx portfolioCache;

    //<editor-fold desc="Constructors">
    public DTOProcessorWatchlistUpdate(
            @NonNull UserBaseKey concernedUser,
            @NonNull WatchlistPositionCacheRx watchlistPositionCache,
            @NonNull PortfolioCacheRx portfolioCache)
    {
        this.concernedUser = concernedUser;
        this.watchlistPositionCache = watchlistPositionCache;
        this.portfolioCache = portfolioCache;
    }
    //</editor-fold>

    @Nullable
    @Override public WatchlistPositionDTO process(@Nullable WatchlistPositionDTO watchlistPositionDTO)
    {
        portfolioCache.invalidate(concernedUser, true);
        if (watchlistPositionDTO != null && watchlistPositionDTO.securityDTO != null)
        {
            watchlistPositionCache.onNext(watchlistPositionDTO.securityDTO.getSecurityId(), watchlistPositionDTO);
        }
        else
        {
            watchlistPositionCache.invalidate(concernedUser);
            Timber.e(new NullPointerException("watchlist or security null " + watchlistPositionDTO), "");
        }
        return watchlistPositionDTO;
    }
}
