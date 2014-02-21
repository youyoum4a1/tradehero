package com.tradehero.th.network.service;

import com.tradehero.th.api.position.PositionCompactId;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import com.tradehero.th.api.watchlist.WatchlistPositionFormDTO;
import com.tradehero.th.api.watchlist.key.PagedWatchlistKey;
import com.tradehero.th.api.watchlist.key.PerPagedWatchlistKey;
import com.tradehero.th.api.watchlist.key.SecurityPerPagedWatchlistKey;
import com.tradehero.th.api.watchlist.key.SkipCacheSecurityPerPagedWatchlistKey;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;

/**
 * Created by xavier on 2/14/14.
 */
@Singleton public class WatchlistServiceWrapper
{
    public static final String TAG = WatchlistServiceWrapper.class.getSimpleName();

    @Inject protected WatchlistService watchlistService;

    @Inject public WatchlistServiceWrapper()
    {
        super();
    }

    //<editor-fold desc="Add/Edit a watch item">
    public WatchlistPositionDTO createWatchlistEntry(WatchlistPositionFormDTO watchlistPositionFormDTO)
    {
        return watchlistService.createWatchlistEntry(watchlistPositionFormDTO);
    }

    public void createWatchlistEntry(WatchlistPositionFormDTO watchlistPositionFormDTO, Callback<WatchlistPositionDTO> callback)
    {
        watchlistService.createWatchlistEntry(watchlistPositionFormDTO, callback);
    }
    //</editor-fold>

    //<editor-fold desc="Add/Edit a watch item">
    public WatchlistPositionDTO updateWatchlistEntry(WatchlistPositionDTO watchlistPositionDTO, WatchlistPositionFormDTO watchlistPositionFormDTO)
    {
        return updateWatchlistEntry(watchlistPositionDTO.getPositionCompactId(), watchlistPositionFormDTO);
    }

    public void updateWatchlistEntry(WatchlistPositionDTO watchlistPositionDTO, WatchlistPositionFormDTO watchlistPositionFormDTO, Callback<WatchlistPositionDTO> callback)
    {
        updateWatchlistEntry(watchlistPositionDTO.getPositionCompactId(), watchlistPositionFormDTO, callback);
    }

    public WatchlistPositionDTO updateWatchlistEntry(PositionCompactId positionId, WatchlistPositionFormDTO watchlistPositionFormDTO)
    {
        return watchlistService.updateWatchlistEntry(positionId.key, watchlistPositionFormDTO);
    }

    public void updateWatchlistEntry(PositionCompactId positionId, WatchlistPositionFormDTO watchlistPositionFormDTO, Callback<WatchlistPositionDTO> callback)
    {
        watchlistService.updateWatchlistEntry(positionId.key, watchlistPositionFormDTO, callback);
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

    public void getAllByUser(PagedWatchlistKey pagedWatchlistKey,
            Callback<WatchlistPositionDTOList> callback)
    {
        if (pagedWatchlistKey instanceof PerPagedWatchlistKey)
        {
            getAllByUser((PerPagedWatchlistKey) pagedWatchlistKey, callback);
        }
        else if (pagedWatchlistKey.page == null)
        {
            watchlistService.getAllByUser(callback);
        }
        else
        {
            watchlistService.getAllByUser(pagedWatchlistKey.page, callback);
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

    public void getAllByUser(PerPagedWatchlistKey perPagedWatchlistKey,
            Callback<WatchlistPositionDTOList> callback)
    {
        if (perPagedWatchlistKey instanceof SecurityPerPagedWatchlistKey)
        {
            getAllByUser((SecurityPerPagedWatchlistKey) perPagedWatchlistKey, callback);
        }
        else if (perPagedWatchlistKey.page == null)
        {
            watchlistService.getAllByUser(callback);
        }
        else if (perPagedWatchlistKey.perPage == null)
        {
            watchlistService.getAllByUser(perPagedWatchlistKey.page, callback);
        }
        else
        {
            watchlistService.getAllByUser(
                    perPagedWatchlistKey.page,
                    perPagedWatchlistKey.perPage,
                    callback);
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

    public void getAllByUser(SecurityPerPagedWatchlistKey securityPerPagedWatchlistKey,
            Callback<WatchlistPositionDTOList> callback)
    {
        if (securityPerPagedWatchlistKey instanceof SkipCacheSecurityPerPagedWatchlistKey)
        {
            getAllByUser((SkipCacheSecurityPerPagedWatchlistKey) securityPerPagedWatchlistKey, callback);
        }
        else if (securityPerPagedWatchlistKey.page == null)
        {
            watchlistService.getAllByUser(callback);
        }
        else if (securityPerPagedWatchlistKey.perPage == null)
        {
            watchlistService.getAllByUser(securityPerPagedWatchlistKey.page, callback);
        }
        else if (securityPerPagedWatchlistKey.securityId == null)
        {
            watchlistService.getAllByUser(
                    securityPerPagedWatchlistKey.page,
                    securityPerPagedWatchlistKey.perPage,
                    callback);
        }
        else
        {
            watchlistService.getAllByUser(
                    securityPerPagedWatchlistKey.page,
                    securityPerPagedWatchlistKey.perPage,
                    securityPerPagedWatchlistKey.securityId,
                    callback);
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

    public void getAllByUser(SkipCacheSecurityPerPagedWatchlistKey skipCacheSecurityPerPagedWatchlistKey,
            Callback<WatchlistPositionDTOList> callback)
    {
        if (skipCacheSecurityPerPagedWatchlistKey.page == null)
        {
            watchlistService.getAllByUser(callback);
        }
        else if (skipCacheSecurityPerPagedWatchlistKey.perPage == null)
        {
            watchlistService.getAllByUser(skipCacheSecurityPerPagedWatchlistKey.page, callback);
        }
        else if (skipCacheSecurityPerPagedWatchlistKey.securityId == null)
        {
            watchlistService.getAllByUser(
                    skipCacheSecurityPerPagedWatchlistKey.page,
                    skipCacheSecurityPerPagedWatchlistKey.perPage,
                    callback);
        }
        else if (skipCacheSecurityPerPagedWatchlistKey.skipCache == null)
        {
            watchlistService.getAllByUser(
                    skipCacheSecurityPerPagedWatchlistKey.page,
                    skipCacheSecurityPerPagedWatchlistKey.perPage,
                    skipCacheSecurityPerPagedWatchlistKey.securityId,
                    callback);
        }
        else
        {
            watchlistService.getAllByUser(
                    skipCacheSecurityPerPagedWatchlistKey.page,
                    skipCacheSecurityPerPagedWatchlistKey.perPage,
                    skipCacheSecurityPerPagedWatchlistKey.securityId,
                    skipCacheSecurityPerPagedWatchlistKey.skipCache,
                    callback);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Delete Watchlist">
    public WatchlistPositionDTO deleteWatchlist(WatchlistPositionDTO watchlistPositionDTO)
    {
        return deleteWatchlist(watchlistPositionDTO.getPositionCompactId());
    }

    public void deleteWatchlist(WatchlistPositionDTO watchlistPositionDTO, Callback<WatchlistPositionDTO> callback)
    {
        deleteWatchlist(watchlistPositionDTO.getPositionCompactId(), callback);
    }

    public WatchlistPositionDTO deleteWatchlist(PositionCompactId positionCompactId)
    {
        return watchlistService.deleteWatchlist(positionCompactId.key);
    }

    public void deleteWatchlist(PositionCompactId positionCompactId, Callback<WatchlistPositionDTO> callback)
    {
        watchlistService.deleteWatchlist(positionCompactId.key, callback);
    }
    //</editor-fold>
}
