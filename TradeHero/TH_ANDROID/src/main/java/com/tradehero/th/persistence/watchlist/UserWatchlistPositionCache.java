package com.tradehero.th.persistence.watchlist;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.api.watchlist.key.PerPagedWatchlistKey;
import com.tradehero.th.network.service.WatchlistServiceWrapper;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class UserWatchlistPositionCache extends StraightDTOCache<UserBaseKey, SecurityIdList>
{
    private static final int DEFAULT_MAX_SIZE = 200;
    private static final int DEFAULT_WATCHLIST_FETCH_SIZE = 100;

    protected Lazy<WatchlistServiceWrapper> watchlistServiceWrapper;
    protected Lazy<WatchlistPositionCache> watchlistPositionCache;

    @Inject public UserWatchlistPositionCache(
            Lazy<WatchlistServiceWrapper> watchlistServiceWrapper,
            Lazy<WatchlistPositionCache> watchlistPositionCache)
    {
        super(DEFAULT_MAX_SIZE);
        this.watchlistServiceWrapper = watchlistServiceWrapper;
        this.watchlistPositionCache = watchlistPositionCache;
    }

    // TODO change the cache to use SkipCacheSecurityPerPagedWatchlistKey in order to provide pagination
    protected PerPagedWatchlistKey createUniqueKey()
    {
        return new PerPagedWatchlistKey(1, DEFAULT_WATCHLIST_FETCH_SIZE);
    }

    @Override protected SecurityIdList fetch(UserBaseKey key) throws Throwable
    {
        return putInternal(
                watchlistServiceWrapper.get().getAllByUser(createUniqueKey()));
    }

    private SecurityIdList putInternal(List<WatchlistPositionDTO> watchlistPositionDTOs)
    {
        SecurityIdList securityIds = new SecurityIdList();
        if (watchlistPositionDTOs != null)
        {
            watchlistPositionCache.get().invalidateAll();
            for (WatchlistPositionDTO watchlistPositionDTO : watchlistPositionDTOs)
            {
                if (watchlistPositionDTO.securityDTO != null)
                {
                    SecurityId securityId = watchlistPositionDTO.securityDTO.getSecurityId();
                    watchlistPositionCache.get().put(securityId, watchlistPositionDTO);
                    securityIds.add(securityId);
                }
            }
        }
        return securityIds;
    }
}
