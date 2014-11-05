package com.tradehero.th.network.service;

import com.tradehero.th.api.position.PositionCompactId;
import com.tradehero.th.api.security.SecurityIntegerIdListForm;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import com.tradehero.th.api.watchlist.WatchlistPositionFormDTO;
import com.tradehero.th.api.watchlist.key.PagedWatchlistKey;
import com.tradehero.th.api.watchlist.key.PerPagedWatchlistKey;
import com.tradehero.th.api.watchlist.key.SecurityPerPagedWatchlistKey;
import com.tradehero.th.api.watchlist.key.SkipCacheSecurityPerPagedWatchlistKey;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.watchlist.DTOProcessorWatchlistCreate;
import com.tradehero.th.models.watchlist.DTOProcessorWatchlistCreateList;
import com.tradehero.th.models.watchlist.DTOProcessorWatchlistDelete;
import com.tradehero.th.models.watchlist.DTOProcessorWatchlistUpdate;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.portfolio.PortfolioCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCacheRx;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import retrofit.Callback;
import rx.Observable;

@Singleton public class WatchlistServiceWrapper
{
    @NonNull private final CurrentUserId currentUserId;
    @NonNull private final WatchlistService watchlistService;
    @NonNull private final WatchlistServiceAsync watchlistServiceAsync;
    @NonNull private final WatchlistServiceRx watchlistServiceRx;
    @NonNull private final Lazy<WatchlistPositionCache> watchlistPositionCache;
    @NonNull private final Lazy<UserWatchlistPositionCache> userWatchlistPositionCache;
    @NonNull private final Lazy<PortfolioCompactCacheRx> portfolioCompactCache;
    @NonNull private final Lazy<PortfolioCacheRx> portfolioCache;

    //<editor-fold desc="Constructors">
    @Inject public WatchlistServiceWrapper(
            @NonNull CurrentUserId currentUserId,
            @NonNull WatchlistService watchlistService,
            @NonNull WatchlistServiceAsync watchlistServiceAsync,
            @NonNull WatchlistServiceRx watchlistServiceRx,
            @NonNull Lazy<WatchlistPositionCache> watchlistPositionCache,
            @NonNull Lazy<UserWatchlistPositionCache> userWatchlistPositionCache,
            @NonNull Lazy<PortfolioCompactCacheRx> portfolioCompactCache,
            @NonNull Lazy<PortfolioCacheRx> portfolioCache)
    {
        super();
        this.currentUserId = currentUserId;
        this.watchlistService = watchlistService;
        this.watchlistServiceAsync = watchlistServiceAsync;
        this.watchlistServiceRx = watchlistServiceRx;
        this.watchlistPositionCache = watchlistPositionCache;
        this.userWatchlistPositionCache = userWatchlistPositionCache;
        this.portfolioCompactCache = portfolioCompactCache;
        this.portfolioCache = portfolioCache;
    }
    //</editor-fold>

    //<editor-fold desc="Add a watch item">
    @NonNull protected DTOProcessorWatchlistCreate createWatchlistCreateProcessor(@NonNull UserBaseKey concernedUser)
    {
        return new DTOProcessorWatchlistCreate(
                watchlistPositionCache.get(),
                concernedUser,
                portfolioCompactCache.get(),
                portfolioCache.get(),
                userWatchlistPositionCache.get());
    }

    @Nullable public Observable<WatchlistPositionDTO> createWatchlistEntryRx(@NonNull WatchlistPositionFormDTO watchlistPositionFormDTO)
    {
        return watchlistServiceRx.createWatchlistEntry(watchlistPositionFormDTO)
                .doOnNext(createWatchlistCreateProcessor(currentUserId.toUserBaseKey()));
    }

    @NonNull public MiddleCallback<WatchlistPositionDTO> createWatchlistEntry(
            @NonNull WatchlistPositionFormDTO watchlistPositionFormDTO,
            @Nullable Callback<WatchlistPositionDTO> callback)
    {
        MiddleCallback<WatchlistPositionDTO> middleCallback = new BaseMiddleCallback<>(
                callback,
                createWatchlistCreateProcessor(currentUserId.toUserBaseKey()));
        watchlistServiceAsync.createWatchlistEntry(watchlistPositionFormDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Edit a watch item">
    @NonNull protected DTOProcessorWatchlistUpdate createWatchlistUpdateProcessor(@NonNull UserBaseKey concernedUser)
    {
        return new DTOProcessorWatchlistUpdate(
                concernedUser,
                watchlistPositionCache.get(),
                portfolioCompactCache.get(),
                portfolioCache.get());
    }

    @Nullable public Observable<WatchlistPositionDTO> updateWatchlistEntryRx(
            @NonNull WatchlistPositionDTO watchlistPositionDTO,
            @NonNull WatchlistPositionFormDTO watchlistPositionFormDTO)
    {
        return updateWatchlistEntryRx(watchlistPositionDTO.getPositionCompactId(), watchlistPositionFormDTO);
    }

    @Nullable public Observable<WatchlistPositionDTO> updateWatchlistEntryRx(
            @NonNull PositionCompactId positionId,
            @NonNull WatchlistPositionFormDTO watchlistPositionFormDTO)
    {
        return watchlistServiceRx.updateWatchlistEntry(positionId.key, watchlistPositionFormDTO)
                .doOnNext(createWatchlistUpdateProcessor(currentUserId.toUserBaseKey()));
    }

    @NonNull public MiddleCallback<WatchlistPositionDTO> updateWatchlistEntry(
            @NonNull PositionCompactId positionId,
            @NonNull WatchlistPositionFormDTO watchlistPositionFormDTO,
            @Nullable Callback<WatchlistPositionDTO> callback)
    {
        MiddleCallback<WatchlistPositionDTO> middleCallback =
                new BaseMiddleCallback<>(callback, createWatchlistUpdateProcessor(currentUserId.toUserBaseKey()));
        watchlistServiceAsync.updateWatchlistEntry(positionId.key, watchlistPositionFormDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Batch Create Watchlist Positions">
    @NonNull protected DTOProcessorWatchlistCreateList createWatchlistPositionBatchCreate(@NonNull UserBaseKey concernedUser)
    {
        return new DTOProcessorWatchlistCreateList(
                watchlistPositionCache.get(),
                concernedUser,
                portfolioCompactCache.get(),
                portfolioCache.get(),
                userWatchlistPositionCache.get());
    }

    @NonNull public Observable<WatchlistPositionDTOList> batchCreateRx(
            @NonNull SecurityIntegerIdListForm securityIntegerIds)
    {
        return watchlistServiceRx.batchCreate(securityIntegerIds)
                .doOnNext(createWatchlistPositionBatchCreate(currentUserId.toUserBaseKey()));
    }

    @NonNull public MiddleCallback<WatchlistPositionDTOList> batchCreate(
            @NonNull SecurityIntegerIdListForm securityIntegerIds,
            @Nullable Callback<WatchlistPositionDTOList> callback)
    {
        MiddleCallback<WatchlistPositionDTOList> middleCallback = new BaseMiddleCallback<>(
                callback,
                createWatchlistPositionBatchCreate(currentUserId.toUserBaseKey()));
        watchlistServiceAsync.batchCreate(securityIntegerIds, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Query for watchlist">
    @NonNull public WatchlistPositionDTOList getAllByUser(@NonNull PagedWatchlistKey pagedWatchlistKey)
    {
        if (pagedWatchlistKey instanceof SkipCacheSecurityPerPagedWatchlistKey)
        {
            SkipCacheSecurityPerPagedWatchlistKey skipCacheSecurityPerPagedWatchlistKey = (SkipCacheSecurityPerPagedWatchlistKey) pagedWatchlistKey;
            return watchlistService.getAllByUser(
                    skipCacheSecurityPerPagedWatchlistKey.page,
                    skipCacheSecurityPerPagedWatchlistKey.perPage,
                    skipCacheSecurityPerPagedWatchlistKey.securityId,
                    skipCacheSecurityPerPagedWatchlistKey.skipCache);
        }
        else if (pagedWatchlistKey instanceof SecurityPerPagedWatchlistKey)
        {
            SecurityPerPagedWatchlistKey securityPerPagedWatchlistKey = (SecurityPerPagedWatchlistKey) pagedWatchlistKey;
            return watchlistService.getAllByUser(
                    securityPerPagedWatchlistKey.page,
                    securityPerPagedWatchlistKey.perPage,
                    securityPerPagedWatchlistKey.securityId,
                    null);
        }
        else if (pagedWatchlistKey instanceof PerPagedWatchlistKey)
        {
            PerPagedWatchlistKey perPagedWatchlistKey = (PerPagedWatchlistKey) pagedWatchlistKey;
            return watchlistService.getAllByUser(
                    perPagedWatchlistKey.page,
                    perPagedWatchlistKey.perPage,
                    null,
                    null);
        }
        return watchlistService.getAllByUser(pagedWatchlistKey.page, null, null, null);
    }

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
    @NonNull protected DTOProcessorWatchlistDelete createWatchlistDeleteProcessor(@NonNull UserBaseKey concernedUser)
    {
        return new DTOProcessorWatchlistDelete(
                watchlistPositionCache.get(),
                concernedUser,
                portfolioCompactCache.get(),
                portfolioCache.get(),
                userWatchlistPositionCache.get());
    }

    @Nullable public Observable<WatchlistPositionDTO> deleteWatchlistRx(@NonNull WatchlistPositionDTO watchlistPositionDTO)
    {
        return deleteWatchlistRx(watchlistPositionDTO.getPositionCompactId());
    }

    @NonNull public MiddleCallback<WatchlistPositionDTO> deleteWatchlist(@NonNull WatchlistPositionDTO watchlistPositionDTO,
            @Nullable Callback<WatchlistPositionDTO> callback)
    {
        return deleteWatchlist(watchlistPositionDTO.getPositionCompactId(), callback);
    }

    @Nullable public Observable<WatchlistPositionDTO> deleteWatchlistRx(@NonNull PositionCompactId positionCompactId)
    {
        return watchlistServiceRx.deleteWatchlist(positionCompactId.key)
                .doOnNext(createWatchlistDeleteProcessor(currentUserId.toUserBaseKey()));
    }

    @NonNull
    public MiddleCallback<WatchlistPositionDTO> deleteWatchlist(@NonNull PositionCompactId positionCompactId,
            @Nullable Callback<WatchlistPositionDTO> callback)
    {
        MiddleCallback<WatchlistPositionDTO> middleCallback = new BaseMiddleCallback<>(
                callback, createWatchlistDeleteProcessor(currentUserId.toUserBaseKey()));
        watchlistServiceAsync.deleteWatchlist(positionCompactId.key, middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
