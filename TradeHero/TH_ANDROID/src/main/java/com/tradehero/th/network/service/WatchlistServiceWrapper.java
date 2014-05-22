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
import retrofit.Callback;

@Singleton public class WatchlistServiceWrapper
{
    private final CurrentUserId currentUserId;
    private final WatchlistService watchlistService;
    private final WatchlistServiceAsync watchlistServiceAsync;
    private final Lazy<WatchlistPositionCache> watchlistPositionCache;
    private final Lazy<UserWatchlistPositionCache> userWatchlistPositionCache;
    private final PortfolioCompactListCache portfolioCompactListCache;

    @Inject public WatchlistServiceWrapper(
            CurrentUserId currentUserId,
            WatchlistService watchlistService,
            WatchlistServiceAsync watchlistServiceAsync,
            Lazy<WatchlistPositionCache> watchlistPositionCache,
            Lazy<UserWatchlistPositionCache> userWatchlistPositionCache,
            PortfolioCompactListCache portfolioCompactListCache)
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
    protected DTOProcessor<WatchlistPositionDTO> createWatchlistUpdateProcessor()
    {
        return new DTOProcessorWatchlistUpdate(watchlistPositionCache.get());
    }

    protected DTOProcessor<WatchlistPositionDTO> createWatchlistCreateProcessor(UserBaseKey concernedUser)
    {
        return new DTOProcessorWatchlistCreate(watchlistPositionCache.get(), concernedUser,
                portfolioCompactListCache, userWatchlistPositionCache.get());
    }

    protected DTOProcessor<WatchlistPositionDTO> createWatchlistDeleteProcessor(UserBaseKey concernedUser)
    {
        return new DTOProcessorWatchlistDelete(watchlistPositionCache.get(), concernedUser,
                portfolioCompactListCache, userWatchlistPositionCache.get());
    }
    //</editor-fold>

    //<editor-fold desc="Add a watch item">
    public WatchlistPositionDTO createWatchlistEntry(WatchlistPositionFormDTO watchlistPositionFormDTO)
    {
        return createWatchlistCreateProcessor(currentUserId.toUserBaseKey()).process(
                watchlistService.createWatchlistEntry(
                        watchlistPositionFormDTO)
        );
    }

    public MiddleCallback<WatchlistPositionDTO> createWatchlistEntry(WatchlistPositionFormDTO watchlistPositionFormDTO, Callback<WatchlistPositionDTO> callback)
    {
        MiddleCallback<WatchlistPositionDTO> middleCallback = new BaseMiddleCallback<>(
                callback,
                createWatchlistCreateProcessor(currentUserId.toUserBaseKey()));
        watchlistServiceAsync.createWatchlistEntry(watchlistPositionFormDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Edit a watch item">
    public WatchlistPositionDTO updateWatchlistEntry(WatchlistPositionDTO watchlistPositionDTO, WatchlistPositionFormDTO watchlistPositionFormDTO)
    {
        return updateWatchlistEntry(watchlistPositionDTO.getPositionCompactId(), watchlistPositionFormDTO);
    }

    public MiddleCallback<WatchlistPositionDTO> updateWatchlistEntry(WatchlistPositionDTO watchlistPositionDTO, WatchlistPositionFormDTO watchlistPositionFormDTO, Callback<WatchlistPositionDTO> callback)
    {
        return updateWatchlistEntry(watchlistPositionDTO.getPositionCompactId(), watchlistPositionFormDTO, callback);
    }

    public WatchlistPositionDTO updateWatchlistEntry(PositionCompactId positionId, WatchlistPositionFormDTO watchlistPositionFormDTO)
    {
        return createWatchlistUpdateProcessor().process(watchlistService.updateWatchlistEntry(positionId.key, watchlistPositionFormDTO));
    }

    public MiddleCallback<WatchlistPositionDTO> updateWatchlistEntry(PositionCompactId positionId, WatchlistPositionFormDTO watchlistPositionFormDTO, Callback<WatchlistPositionDTO> callback)
    {
        MiddleCallback<WatchlistPositionDTO> middleCallback = new BaseMiddleCallback<WatchlistPositionDTO>(callback, createWatchlistUpdateProcessor());
        watchlistServiceAsync.updateWatchlistEntry(positionId.key, watchlistPositionFormDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Query for watchlist">
    public WatchlistPositionDTOList getAllByUser(PagedWatchlistKey pagedWatchlistKey)
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

    public MiddleCallback<WatchlistPositionDTOList> getAllByUser(PagedWatchlistKey pagedWatchlistKey,
            Callback<WatchlistPositionDTOList> callback)
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
    public WatchlistPositionDTO deleteWatchlist(WatchlistPositionDTO watchlistPositionDTO)
    {
        return deleteWatchlist(watchlistPositionDTO.getPositionCompactId());
    }

    public MiddleCallback<WatchlistPositionDTO> deleteWatchlist(WatchlistPositionDTO watchlistPositionDTO, Callback<WatchlistPositionDTO> callback)
    {
        return deleteWatchlist(watchlistPositionDTO.getPositionCompactId(), callback);
    }

    public WatchlistPositionDTO deleteWatchlist(PositionCompactId positionCompactId)
    {
        return createWatchlistDeleteProcessor(currentUserId.toUserBaseKey()).process(
                watchlistService.deleteWatchlist(positionCompactId.key));
    }

    public MiddleCallback<WatchlistPositionDTO> deleteWatchlist(PositionCompactId positionCompactId, Callback<WatchlistPositionDTO> callback)
    {
        MiddleCallback<WatchlistPositionDTO> middleCallback = new BaseMiddleCallback<>(
                callback, createWatchlistDeleteProcessor(currentUserId.toUserBaseKey()));
        watchlistServiceAsync.deleteWatchlist(positionCompactId.key, middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
