package com.ayondo.academy.network.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.ayondo.academy.api.position.PositionCompactId;
import com.ayondo.academy.api.security.SecurityIntegerIdListForm;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.api.watchlist.WatchlistPositionDTO;
import com.ayondo.academy.api.watchlist.WatchlistPositionDTOList;
import com.ayondo.academy.api.watchlist.WatchlistPositionFormDTO;
import com.ayondo.academy.api.watchlist.key.PagedWatchlistKey;
import com.ayondo.academy.api.watchlist.key.PerPagedWatchlistKey;
import com.ayondo.academy.api.watchlist.key.SecurityPerPagedWatchlistKey;
import com.ayondo.academy.api.watchlist.key.SkipCacheSecurityPerPagedWatchlistKey;
import com.ayondo.academy.models.watchlist.DTOProcessorWatchlistCreate;
import com.ayondo.academy.models.watchlist.DTOProcessorWatchlistCreateList;
import com.ayondo.academy.models.watchlist.DTOProcessorWatchlistDelete;
import com.ayondo.academy.models.watchlist.DTOProcessorWatchlistUpdate;
import com.ayondo.academy.persistence.portfolio.PortfolioCacheRx;
import com.ayondo.academy.persistence.watchlist.UserWatchlistPositionCacheRx;
import com.ayondo.academy.persistence.watchlist.WatchlistPositionCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton public class WatchlistServiceWrapper
{
    @NonNull private final CurrentUserId currentUserId;
    @NonNull private final WatchlistServiceRx watchlistServiceRx;
    @NonNull private final Lazy<WatchlistPositionCacheRx> watchlistPositionCache;
    @NonNull private final Lazy<UserWatchlistPositionCacheRx> userWatchlistPositionCache;
    @NonNull private final Lazy<PortfolioCacheRx> portfolioCache;

    //<editor-fold desc="Constructors">
    @Inject public WatchlistServiceWrapper(
            @NonNull CurrentUserId currentUserId,
            @NonNull WatchlistServiceRx watchlistServiceRx,
            @NonNull Lazy<WatchlistPositionCacheRx> watchlistPositionCache,
            @NonNull Lazy<UserWatchlistPositionCacheRx> userWatchlistPositionCache,
            @NonNull Lazy<PortfolioCacheRx> portfolioCache)
    {
        super();
        this.currentUserId = currentUserId;
        this.watchlistServiceRx = watchlistServiceRx;
        this.watchlistPositionCache = watchlistPositionCache;
        this.userWatchlistPositionCache = userWatchlistPositionCache;
        this.portfolioCache = portfolioCache;
    }
    //</editor-fold>

    //<editor-fold desc="Add a watch item">
    @Nullable public Observable<WatchlistPositionDTO> createWatchlistEntryRx(@NonNull WatchlistPositionFormDTO watchlistPositionFormDTO)
    {
        return watchlistServiceRx.createWatchlistEntry(watchlistPositionFormDTO)
                .map(new DTOProcessorWatchlistCreate(
                        watchlistPositionCache.get(),
                        currentUserId.toUserBaseKey(),
                        portfolioCache.get(),
                        userWatchlistPositionCache.get()));
    }
    //</editor-fold>

    //<editor-fold desc="Edit a watch item">
    @NonNull public Observable<WatchlistPositionDTO> updateWatchlistEntryRx(
            @NonNull PositionCompactId positionId,
            @NonNull WatchlistPositionFormDTO watchlistPositionFormDTO)
    {
        return watchlistServiceRx.updateWatchlistEntry(positionId.key, watchlistPositionFormDTO)
                .map(new DTOProcessorWatchlistUpdate(
                        currentUserId.toUserBaseKey(),
                        watchlistPositionCache.get(),
                        portfolioCache.get()));
    }
    //</editor-fold>

    //<editor-fold desc="Batch Create Watchlist Positions">
    @NonNull public Observable<WatchlistPositionDTOList> batchCreateRx(
            @NonNull SecurityIntegerIdListForm securityIntegerIds)
    {
        return watchlistServiceRx.batchCreate(securityIntegerIds)
                .map(new DTOProcessorWatchlistCreateList(
                        watchlistPositionCache.get(),
                        currentUserId.toUserBaseKey(),
                        portfolioCache.get(),
                        userWatchlistPositionCache.get()));
    }
    //</editor-fold>

    //<editor-fold desc="Query for watchlist">
    @NonNull public Observable<WatchlistPositionDTOList> getAllByUserRx(@NonNull PagedWatchlistKey pagedWatchlistKey)
    {
        if (pagedWatchlistKey instanceof SkipCacheSecurityPerPagedWatchlistKey)
        {
            SkipCacheSecurityPerPagedWatchlistKey skipCacheSecurityPerPagedWatchlistKey = (SkipCacheSecurityPerPagedWatchlistKey) pagedWatchlistKey;
            return watchlistServiceRx.getAllByUser(
                    skipCacheSecurityPerPagedWatchlistKey.page,
                    skipCacheSecurityPerPagedWatchlistKey.perPage,
                    skipCacheSecurityPerPagedWatchlistKey.securityId,
                    skipCacheSecurityPerPagedWatchlistKey.skipCache);
        }
        else if (pagedWatchlistKey instanceof SecurityPerPagedWatchlistKey)
        {
            SecurityPerPagedWatchlistKey securityPerPagedWatchlistKey = (SecurityPerPagedWatchlistKey) pagedWatchlistKey;
            return watchlistServiceRx.getAllByUser(
                    securityPerPagedWatchlistKey.page,
                    securityPerPagedWatchlistKey.perPage,
                    securityPerPagedWatchlistKey.securityId,
                    null);
        }
        else if (pagedWatchlistKey instanceof PerPagedWatchlistKey)
        {
            PerPagedWatchlistKey perPagedWatchlistKey = (PerPagedWatchlistKey) pagedWatchlistKey;
            return watchlistServiceRx.getAllByUser(
                    perPagedWatchlistKey.page,
                    perPagedWatchlistKey.perPage,
                    null,
                    null);
        }
        return watchlistServiceRx.getAllByUser(pagedWatchlistKey.page, null, null, null);
    }
    //</editor-fold>

    //<editor-fold desc="Delete Watchlist">
    @NonNull public Observable<WatchlistPositionDTO> deleteWatchlistRx(@NonNull PositionCompactId positionCompactId)
    {
        return watchlistServiceRx.deleteWatchlist(positionCompactId.key)
                .map(new DTOProcessorWatchlistDelete(
                        watchlistPositionCache.get(),
                        currentUserId.toUserBaseKey(),
                        portfolioCache.get(),
                        userWatchlistPositionCache.get()));
    }
    //</editor-fold>
}
