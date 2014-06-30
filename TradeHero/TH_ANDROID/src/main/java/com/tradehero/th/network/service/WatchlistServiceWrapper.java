package com.tradehero.th.network.service;

import com.tradehero.th.api.position.PositionCompactId;
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
import com.tradehero.th.models.watchlist.DTOProcessorWatchlistDelete;
import com.tradehero.th.models.watchlist.DTOProcessorWatchlistUpdate;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;

@Singleton public class WatchlistServiceWrapper
{
    @NotNull private final CurrentUserId currentUserId;
    @NotNull private final WatchlistService watchlistService;
    @NotNull private final WatchlistServiceAsync watchlistServiceAsync;
    @NotNull private final Lazy<WatchlistPositionCache> watchlistPositionCache;
    @NotNull private final Lazy<UserWatchlistPositionCache> userWatchlistPositionCache;
    @NotNull private final PortfolioCompactListCache portfolioCompactListCache;

    @Inject public WatchlistServiceWrapper(
            @NotNull CurrentUserId currentUserId,
            @NotNull WatchlistService watchlistService,
            @NotNull WatchlistServiceAsync watchlistServiceAsync,
            @NotNull Lazy<WatchlistPositionCache> watchlistPositionCache,
            @NotNull Lazy<UserWatchlistPositionCache> userWatchlistPositionCache,
            @NotNull PortfolioCompactListCache portfolioCompactListCache)
    {
        super();
        this.currentUserId = currentUserId;
        this.watchlistService = watchlistService;
        this.watchlistServiceAsync = watchlistServiceAsync;
        this.watchlistPositionCache = watchlistPositionCache;
        this.userWatchlistPositionCache = userWatchlistPositionCache;
        this.portfolioCompactListCache = portfolioCompactListCache;
    }

    //<editor-fold desc="DTO Processors">
    @NotNull protected DTOProcessor<WatchlistPositionDTO> createWatchlistUpdateProcessor()
    {
        return new DTOProcessorWatchlistUpdate(watchlistPositionCache.get());
    }

    @NotNull protected DTOProcessor<WatchlistPositionDTO> createWatchlistCreateProcessor(@NotNull UserBaseKey concernedUser)
    {
        return new DTOProcessorWatchlistCreate(
                watchlistPositionCache.get(),
                concernedUser,
                portfolioCompactListCache,
                userWatchlistPositionCache.get());
    }

    @NotNull protected DTOProcessor<WatchlistPositionDTO> createWatchlistDeleteProcessor(@NotNull UserBaseKey concernedUser)
    {
        return new DTOProcessorWatchlistDelete(
                watchlistPositionCache.get(),
                concernedUser,
                portfolioCompactListCache,
                userWatchlistPositionCache.get());
    }
    //</editor-fold>

    //<editor-fold desc="Add a watch item">
    @Nullable public WatchlistPositionDTO createWatchlistEntry(@NotNull WatchlistPositionFormDTO watchlistPositionFormDTO)
    {
        return createWatchlistCreateProcessor(
                currentUserId.toUserBaseKey()).process(
                    watchlistService.createWatchlistEntry(
                        watchlistPositionFormDTO)
        );
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
    @Nullable public WatchlistPositionDTO updateWatchlistEntry(
            @NotNull WatchlistPositionDTO watchlistPositionDTO,
            @NotNull WatchlistPositionFormDTO watchlistPositionFormDTO)
    {
        return updateWatchlistEntry(watchlistPositionDTO.getPositionCompactId(), watchlistPositionFormDTO);
    }

    @NotNull public MiddleCallback<WatchlistPositionDTO> updateWatchlistEntry(
            @NotNull WatchlistPositionDTO watchlistPositionDTO,
            @NotNull WatchlistPositionFormDTO watchlistPositionFormDTO,
            @Nullable Callback<WatchlistPositionDTO> callback)
    {
        return updateWatchlistEntry(watchlistPositionDTO.getPositionCompactId(), watchlistPositionFormDTO, callback);
    }

    @Nullable public WatchlistPositionDTO updateWatchlistEntry(
            @NotNull PositionCompactId positionId,
            @NotNull WatchlistPositionFormDTO watchlistPositionFormDTO)
    {
        return createWatchlistUpdateProcessor().process(watchlistService.updateWatchlistEntry(positionId.key, watchlistPositionFormDTO));
    }

    @NotNull public MiddleCallback<WatchlistPositionDTO> updateWatchlistEntry(
            @NotNull PositionCompactId positionId,
            @NotNull WatchlistPositionFormDTO watchlistPositionFormDTO,
            @Nullable Callback<WatchlistPositionDTO> callback)
    {
        MiddleCallback<WatchlistPositionDTO> middleCallback = new BaseMiddleCallback<>(callback, createWatchlistUpdateProcessor());
        watchlistServiceAsync.updateWatchlistEntry(positionId.key, watchlistPositionFormDTO, middleCallback);
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

    @NotNull public MiddleCallback<WatchlistPositionDTOList> getAllByUser(
            @NotNull PagedWatchlistKey pagedWatchlistKey,
            @Nullable Callback<WatchlistPositionDTOList> callback)
    {
        MiddleCallback<WatchlistPositionDTOList> middleCallback = new BaseMiddleCallback<>(callback);
        if (pagedWatchlistKey instanceof SkipCacheSecurityPerPagedWatchlistKey)
        {
            SkipCacheSecurityPerPagedWatchlistKey skipCacheSecurityPerPagedWatchlistKey = (SkipCacheSecurityPerPagedWatchlistKey) pagedWatchlistKey;
            watchlistServiceAsync.getAllByUser(
                    skipCacheSecurityPerPagedWatchlistKey.page,
                    skipCacheSecurityPerPagedWatchlistKey.perPage,
                    skipCacheSecurityPerPagedWatchlistKey.securityId,
                    skipCacheSecurityPerPagedWatchlistKey.skipCache,
                    middleCallback);
        }
        else if (pagedWatchlistKey instanceof SecurityPerPagedWatchlistKey)
        {
            SecurityPerPagedWatchlistKey securityPerPagedWatchlistKey = (SecurityPerPagedWatchlistKey) pagedWatchlistKey;
            watchlistServiceAsync.getAllByUser(
                    securityPerPagedWatchlistKey.page,
                    securityPerPagedWatchlistKey.perPage,
                    securityPerPagedWatchlistKey.securityId,
                    null,
                    middleCallback);
        }
        else if (pagedWatchlistKey instanceof PerPagedWatchlistKey)
        {
            PerPagedWatchlistKey perPagedWatchlistKey = (PerPagedWatchlistKey) pagedWatchlistKey;
            watchlistServiceAsync.getAllByUser(
                    perPagedWatchlistKey.page,
                    perPagedWatchlistKey.perPage,
                    null,
                    null,
                    middleCallback);
        }
        else
        {
            watchlistServiceAsync.getAllByUser(pagedWatchlistKey.page, null, null, null, middleCallback);
        }
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Delete Watchlist">
    @Nullable public WatchlistPositionDTO deleteWatchlist(@NotNull WatchlistPositionDTO watchlistPositionDTO)
    {
        return deleteWatchlist(watchlistPositionDTO.getPositionCompactId());
    }

    @NotNull public MiddleCallback<WatchlistPositionDTO> deleteWatchlist(@NotNull WatchlistPositionDTO watchlistPositionDTO, @Nullable Callback<WatchlistPositionDTO> callback)
    {
        return deleteWatchlist(watchlistPositionDTO.getPositionCompactId(), callback);
    }

    @Nullable public WatchlistPositionDTO deleteWatchlist(@NotNull PositionCompactId positionCompactId)
    {
        return createWatchlistDeleteProcessor(currentUserId.toUserBaseKey()).process(
                watchlistService.deleteWatchlist(positionCompactId.key));
    }

    @NotNull
    public MiddleCallback<WatchlistPositionDTO> deleteWatchlist(@NotNull PositionCompactId positionCompactId, @Nullable Callback<WatchlistPositionDTO> callback)
    {
        MiddleCallback<WatchlistPositionDTO> middleCallback = new BaseMiddleCallback<>(
                callback, createWatchlistDeleteProcessor(currentUserId.toUserBaseKey()));
        watchlistServiceAsync.deleteWatchlist(positionCompactId.key, middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
