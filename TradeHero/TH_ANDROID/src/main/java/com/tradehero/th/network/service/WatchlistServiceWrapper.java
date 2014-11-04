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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import rx.Observable;

@Singleton public class WatchlistServiceWrapper
{
    @NotNull private final CurrentUserId currentUserId;
    @NotNull private final WatchlistService watchlistService;
    @NotNull private final WatchlistServiceAsync watchlistServiceAsync;
    @NotNull private final WatchlistServiceRx watchlistServiceRx;
    @NotNull private final Lazy<WatchlistPositionCache> watchlistPositionCache;
    @NotNull private final Lazy<UserWatchlistPositionCache> userWatchlistPositionCache;
    @NotNull private final Lazy<PortfolioCompactCacheRx> portfolioCompactCache;
    @NotNull private final Lazy<PortfolioCacheRx> portfolioCache;

    //<editor-fold desc="Constructors">
    @Inject public WatchlistServiceWrapper(
            @NotNull CurrentUserId currentUserId,
            @NotNull WatchlistService watchlistService,
            @NotNull WatchlistServiceAsync watchlistServiceAsync,
            @NotNull WatchlistServiceRx watchlistServiceRx,
            @NotNull Lazy<WatchlistPositionCache> watchlistPositionCache,
            @NotNull Lazy<UserWatchlistPositionCache> userWatchlistPositionCache,
            @NotNull Lazy<PortfolioCompactCacheRx> portfolioCompactCache,
            @NotNull Lazy<PortfolioCacheRx> portfolioCache)
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
    @NotNull protected DTOProcessorWatchlistCreate createWatchlistCreateProcessor(@NotNull UserBaseKey concernedUser)
    {
        return new DTOProcessorWatchlistCreate(
                watchlistPositionCache.get(),
                concernedUser,
                portfolioCompactCache.get(),
                portfolioCache.get(),
                userWatchlistPositionCache.get());
    }

    @Nullable public Observable<WatchlistPositionDTO> createWatchlistEntryRx(@NotNull WatchlistPositionFormDTO watchlistPositionFormDTO)
    {
        return watchlistServiceRx.createWatchlistEntry(watchlistPositionFormDTO)
                .doOnNext(createWatchlistCreateProcessor(currentUserId.toUserBaseKey()));
    }

    @NotNull public MiddleCallback<WatchlistPositionDTO> createWatchlistEntry(
            @NotNull WatchlistPositionFormDTO watchlistPositionFormDTO,
            @Nullable Callback<WatchlistPositionDTO> callback)
    {
        @NotNull MiddleCallback<WatchlistPositionDTO> middleCallback = new BaseMiddleCallback<>(
                callback,
                createWatchlistCreateProcessor(currentUserId.toUserBaseKey()));
        watchlistServiceAsync.createWatchlistEntry(watchlistPositionFormDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Edit a watch item">
    @NotNull protected DTOProcessorWatchlistUpdate createWatchlistUpdateProcessor(@NotNull UserBaseKey concernedUser)
    {
        return new DTOProcessorWatchlistUpdate(
                concernedUser,
                watchlistPositionCache.get(),
                portfolioCompactCache.get(),
                portfolioCache.get());
    }

    @Nullable public Observable<WatchlistPositionDTO> updateWatchlistEntryRx(
            @NotNull WatchlistPositionDTO watchlistPositionDTO,
            @NotNull WatchlistPositionFormDTO watchlistPositionFormDTO)
    {
        return updateWatchlistEntryRx(watchlistPositionDTO.getPositionCompactId(), watchlistPositionFormDTO);
    }

    @Nullable public Observable<WatchlistPositionDTO> updateWatchlistEntryRx(
            @NotNull PositionCompactId positionId,
            @NotNull WatchlistPositionFormDTO watchlistPositionFormDTO)
    {
        return watchlistServiceRx.updateWatchlistEntry(positionId.key, watchlistPositionFormDTO)
                .doOnNext(createWatchlistUpdateProcessor(currentUserId.toUserBaseKey()));
    }

    @NotNull public MiddleCallback<WatchlistPositionDTO> updateWatchlistEntry(
            @NotNull PositionCompactId positionId,
            @NotNull WatchlistPositionFormDTO watchlistPositionFormDTO,
            @Nullable Callback<WatchlistPositionDTO> callback)
    {
        MiddleCallback<WatchlistPositionDTO> middleCallback =
                new BaseMiddleCallback<>(callback, createWatchlistUpdateProcessor(currentUserId.toUserBaseKey()));
        watchlistServiceAsync.updateWatchlistEntry(positionId.key, watchlistPositionFormDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Batch Create Watchlist Positions">
    @NotNull protected DTOProcessorWatchlistCreateList createWatchlistPositionBatchCreate(@NotNull UserBaseKey concernedUser)
    {
        return new DTOProcessorWatchlistCreateList(
                watchlistPositionCache.get(),
                concernedUser,
                portfolioCompactCache.get(),
                portfolioCache.get(),
                userWatchlistPositionCache.get());
    }

    @NotNull public Observable<WatchlistPositionDTOList> batchCreateRx(
            @NotNull SecurityIntegerIdListForm securityIntegerIds)
    {
        return watchlistServiceRx.batchCreate(securityIntegerIds)
                .doOnNext(createWatchlistPositionBatchCreate(currentUserId.toUserBaseKey()));
    }

    @NotNull public MiddleCallback<WatchlistPositionDTOList> batchCreate(
            @NotNull SecurityIntegerIdListForm securityIntegerIds,
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
    @NotNull public WatchlistPositionDTOList getAllByUser(@NotNull PagedWatchlistKey pagedWatchlistKey)
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

    @NotNull public Observable<WatchlistPositionDTOList> getAllByUserRx(@NotNull PagedWatchlistKey pagedWatchlistKey)
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
    @NotNull protected DTOProcessorWatchlistDelete createWatchlistDeleteProcessor(@NotNull UserBaseKey concernedUser)
    {
        return new DTOProcessorWatchlistDelete(
                watchlistPositionCache.get(),
                concernedUser,
                portfolioCompactCache.get(),
                portfolioCache.get(),
                userWatchlistPositionCache.get());
    }

    @Nullable public Observable<WatchlistPositionDTO> deleteWatchlistRx(@NotNull WatchlistPositionDTO watchlistPositionDTO)
    {
        return deleteWatchlistRx(watchlistPositionDTO.getPositionCompactId());
    }

    @NotNull public MiddleCallback<WatchlistPositionDTO> deleteWatchlist(@NotNull WatchlistPositionDTO watchlistPositionDTO,
            @Nullable Callback<WatchlistPositionDTO> callback)
    {
        return deleteWatchlist(watchlistPositionDTO.getPositionCompactId(), callback);
    }

    @Nullable public Observable<WatchlistPositionDTO> deleteWatchlistRx(@NotNull PositionCompactId positionCompactId)
    {
        return watchlistServiceRx.deleteWatchlist(positionCompactId.key)
                .doOnNext(createWatchlistDeleteProcessor(currentUserId.toUserBaseKey()));
    }

    @NotNull
    public MiddleCallback<WatchlistPositionDTO> deleteWatchlist(@NotNull PositionCompactId positionCompactId,
            @Nullable Callback<WatchlistPositionDTO> callback)
    {
        MiddleCallback<WatchlistPositionDTO> middleCallback = new BaseMiddleCallback<>(
                callback, createWatchlistDeleteProcessor(currentUserId.toUserBaseKey()));
        watchlistServiceAsync.deleteWatchlist(positionCompactId.key, middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
