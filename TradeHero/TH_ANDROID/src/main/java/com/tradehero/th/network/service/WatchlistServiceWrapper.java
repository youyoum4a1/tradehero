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
        if (pagedWatchlistKey instanceof PerPagedWatchlistKey)
        {
            return getAllByUser((PerPagedWatchlistKey) pagedWatchlistKey);
        }

        if (pagedWatchlistKey.page == null)
        {
            return watchlistService.getAllByUser();
        }
        return watchlistService.getAllByUser(pagedWatchlistKey.page);
    }

    public MiddleCallback<WatchlistPositionDTOList> getAllByUser(PagedWatchlistKey pagedWatchlistKey,
            Callback<WatchlistPositionDTOList> callback)
    {
        if (pagedWatchlistKey instanceof PerPagedWatchlistKey)
        {
            return getAllByUser((PerPagedWatchlistKey) pagedWatchlistKey, callback);
        }
        else
        {
            MiddleCallback<WatchlistPositionDTOList> middleCallback = new BaseMiddleCallback<>(callback);
            if (pagedWatchlistKey.page == null)
            {
                watchlistServiceAsync.getAllByUser(middleCallback);
            }
            else
            {
                watchlistServiceAsync.getAllByUser(pagedWatchlistKey.page, middleCallback);
            }
            return middleCallback;
        }
    }

    public WatchlistPositionDTOList getAllByUser(PerPagedWatchlistKey perPagedWatchlistKey)
    {
        if (perPagedWatchlistKey instanceof SecurityPerPagedWatchlistKey)
        {
            return getAllByUser((SecurityPerPagedWatchlistKey) perPagedWatchlistKey);
        }

        if (perPagedWatchlistKey.page == null)
        {
            return watchlistService.getAllByUser();
        }
        if (perPagedWatchlistKey.perPage == null)
        {
            return watchlistService.getAllByUser(perPagedWatchlistKey.page);
        }
        return watchlistService.getAllByUser(
                perPagedWatchlistKey.page,
                perPagedWatchlistKey.perPage);
    }

    public MiddleCallback<WatchlistPositionDTOList> getAllByUser(PerPagedWatchlistKey perPagedWatchlistKey,
            Callback<WatchlistPositionDTOList> callback)
    {
        if (perPagedWatchlistKey instanceof SecurityPerPagedWatchlistKey)
        {
            return getAllByUser((SecurityPerPagedWatchlistKey) perPagedWatchlistKey, callback);
        }
        else
        {
            MiddleCallback<WatchlistPositionDTOList> middleCallback = new BaseMiddleCallback<>(callback);
            if (perPagedWatchlistKey.page == null)
            {
                watchlistServiceAsync.getAllByUser(middleCallback);
            }
            else if (perPagedWatchlistKey.perPage == null)
            {
                watchlistServiceAsync.getAllByUser(perPagedWatchlistKey.page, middleCallback);
            }
            else
            {
                watchlistServiceAsync.getAllByUser(
                        perPagedWatchlistKey.page,
                        perPagedWatchlistKey.perPage,
                        middleCallback);
            }
            return middleCallback;
        }
    }

    public WatchlistPositionDTOList getAllByUser(SecurityPerPagedWatchlistKey securityPerPagedWatchlistKey)
    {
        if (securityPerPagedWatchlistKey instanceof SkipCacheSecurityPerPagedWatchlistKey)
        {
            return getAllByUser((SkipCacheSecurityPerPagedWatchlistKey) securityPerPagedWatchlistKey);
        }

        if (securityPerPagedWatchlistKey.page == null)
        {
            return watchlistService.getAllByUser();
        }
        if (securityPerPagedWatchlistKey.perPage == null)
        {
            return watchlistService.getAllByUser(securityPerPagedWatchlistKey.page);
        }
        if (securityPerPagedWatchlistKey.securityId == null)
        {
            return watchlistService.getAllByUser(
                    securityPerPagedWatchlistKey.page,
                    securityPerPagedWatchlistKey.perPage);
        }
        return watchlistService.getAllByUser(
                securityPerPagedWatchlistKey.page,
                securityPerPagedWatchlistKey.perPage,
                securityPerPagedWatchlistKey.securityId);
    }

    public MiddleCallback<WatchlistPositionDTOList> getAllByUser(SecurityPerPagedWatchlistKey securityPerPagedWatchlistKey,
            Callback<WatchlistPositionDTOList> callback)
    {
        if (securityPerPagedWatchlistKey instanceof SkipCacheSecurityPerPagedWatchlistKey)
        {
            return getAllByUser((SkipCacheSecurityPerPagedWatchlistKey) securityPerPagedWatchlistKey, callback);
        }
        else
        {
            MiddleCallback<WatchlistPositionDTOList> middleCallback = new BaseMiddleCallback<>(callback);
            if (securityPerPagedWatchlistKey.page == null)
            {
                watchlistServiceAsync.getAllByUser(middleCallback);
            }
            else if (securityPerPagedWatchlistKey.perPage == null)
            {
                watchlistServiceAsync.getAllByUser(securityPerPagedWatchlistKey.page, middleCallback);
            }
            else if (securityPerPagedWatchlistKey.securityId == null)
            {
                watchlistServiceAsync.getAllByUser(
                        securityPerPagedWatchlistKey.page,
                        securityPerPagedWatchlistKey.perPage,
                        middleCallback);
            }
            else
            {
                watchlistServiceAsync.getAllByUser(
                        securityPerPagedWatchlistKey.page,
                        securityPerPagedWatchlistKey.perPage,
                        securityPerPagedWatchlistKey.securityId,
                        middleCallback);
            }
            return middleCallback;
        }
    }

    public WatchlistPositionDTOList getAllByUser(SkipCacheSecurityPerPagedWatchlistKey skipCacheSecurityPerPagedWatchlistKey)
    {
        if (skipCacheSecurityPerPagedWatchlistKey.page == null)
        {
            return watchlistService.getAllByUser();
        }
        if (skipCacheSecurityPerPagedWatchlistKey.perPage == null)
        {
            return watchlistService.getAllByUser(skipCacheSecurityPerPagedWatchlistKey.page);
        }
        if (skipCacheSecurityPerPagedWatchlistKey.securityId == null)
        {
            return watchlistService.getAllByUser(
                    skipCacheSecurityPerPagedWatchlistKey.page,
                    skipCacheSecurityPerPagedWatchlistKey.perPage);
        }
        if (skipCacheSecurityPerPagedWatchlistKey.skipCache == null)
        {
            return watchlistService.getAllByUser(
                    skipCacheSecurityPerPagedWatchlistKey.page,
                    skipCacheSecurityPerPagedWatchlistKey.perPage,
                    skipCacheSecurityPerPagedWatchlistKey.securityId);
        }
        return watchlistService.getAllByUser(
                skipCacheSecurityPerPagedWatchlistKey.page,
                skipCacheSecurityPerPagedWatchlistKey.perPage,
                skipCacheSecurityPerPagedWatchlistKey.securityId,
                skipCacheSecurityPerPagedWatchlistKey.skipCache);
    }

    public MiddleCallback<WatchlistPositionDTOList> getAllByUser(SkipCacheSecurityPerPagedWatchlistKey skipCacheSecurityPerPagedWatchlistKey,
            Callback<WatchlistPositionDTOList> callback)
    {
        MiddleCallback<WatchlistPositionDTOList> middleCallback = new BaseMiddleCallback<>(callback);
        if (skipCacheSecurityPerPagedWatchlistKey.page == null)
        {
            watchlistServiceAsync.getAllByUser(middleCallback);
        }
        else if (skipCacheSecurityPerPagedWatchlistKey.perPage == null)
        {
            watchlistServiceAsync.getAllByUser(skipCacheSecurityPerPagedWatchlistKey.page, middleCallback);
        }
        else if (skipCacheSecurityPerPagedWatchlistKey.securityId == null)
        {
            watchlistServiceAsync.getAllByUser(
                    skipCacheSecurityPerPagedWatchlistKey.page,
                    skipCacheSecurityPerPagedWatchlistKey.perPage,
                    middleCallback);
        }
        else if (skipCacheSecurityPerPagedWatchlistKey.skipCache == null)
        {
            watchlistServiceAsync.getAllByUser(
                    skipCacheSecurityPerPagedWatchlistKey.page,
                    skipCacheSecurityPerPagedWatchlistKey.perPage,
                    skipCacheSecurityPerPagedWatchlistKey.securityId,
                    middleCallback);
        }
        else
        {
            watchlistServiceAsync.getAllByUser(
                    skipCacheSecurityPerPagedWatchlistKey.page,
                    skipCacheSecurityPerPagedWatchlistKey.perPage,
                    skipCacheSecurityPerPagedWatchlistKey.securityId,
                    skipCacheSecurityPerPagedWatchlistKey.skipCache,
                    middleCallback);
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
