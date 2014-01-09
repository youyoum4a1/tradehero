package com.tradehero.th.persistence.watchlist;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.network.service.WatchlistService;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: tho Date: 12/3/13 Time: 5:54 PM Copyright (c) TradeHero */
@Singleton public class UserWatchlistPositionCache extends StraightDTOCache<UserBaseKey, SecurityIdList>
{
    private static final int DEFAULT_MAX_SIZE = 200;
    private static final int DEFAULT_WATCHLIST_FETCH_SIZE = 100;

    @Inject protected Lazy<WatchlistService> watchlistService;

    @Inject protected Lazy<WatchlistPositionCache> watchlistPositionCache;

    @Inject public UserWatchlistPositionCache()
    {
        super(DEFAULT_MAX_SIZE);
    }

    @Override protected SecurityIdList fetch(UserBaseKey key) throws Throwable
    {
        return putInternal(watchlistService.get().getAllByUser(1, DEFAULT_WATCHLIST_FETCH_SIZE));
    }

    private SecurityIdList putInternal(List<WatchlistPositionDTO> watchlistPositionDTOs)
    {
        SecurityIdList securityIds = new SecurityIdList();
        if (watchlistPositionDTOs != null)
        {
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
