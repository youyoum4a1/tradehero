package com.tradehero.th.models.watchlist;

import android.support.annotation.NonNull;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.portfolio.PortfolioCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCacheRx;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCacheRx;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCacheRx;
import rx.functions.Action1;
import timber.log.Timber;

public class DTOProcessorWatchlistDelete implements DTOProcessor<WatchlistPositionDTO>,
        Action1<WatchlistPositionDTO>
{
    @NonNull private final UserBaseKey concernedUser;
    @NonNull private final WatchlistPositionCacheRx watchlistPositionCache;
    @NonNull private final PortfolioCompactCacheRx portfolioCompactCache;
    @NonNull private final PortfolioCacheRx portfolioCache;
    @NonNull private final UserWatchlistPositionCacheRx userWatchlistPositionCache;

    //<editor-fold desc="Constructors">
    public DTOProcessorWatchlistDelete(
            @NonNull WatchlistPositionCacheRx watchlistPositionCache,
            @NonNull UserBaseKey concernedUser,
            @NonNull PortfolioCompactCacheRx portfolioCompactCache,
            @NonNull PortfolioCacheRx portfolioCache,
            @NonNull UserWatchlistPositionCacheRx userWatchlistPositionCache)
    {
        super();
        this.concernedUser = concernedUser;
        this.watchlistPositionCache = watchlistPositionCache;
        this.portfolioCompactCache = portfolioCompactCache;
        this.portfolioCache = portfolioCache;
        this.userWatchlistPositionCache = userWatchlistPositionCache;
    }
    //</editor-fold>

    @Override public WatchlistPositionDTO process(@NonNull WatchlistPositionDTO watchlistPositionDTO)
    {
        portfolioCompactCache.invalidate(concernedUser, true);
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

    @Override public void call(WatchlistPositionDTO watchlistPositionDTO)
    {
        process(watchlistPositionDTO);
    }
}
