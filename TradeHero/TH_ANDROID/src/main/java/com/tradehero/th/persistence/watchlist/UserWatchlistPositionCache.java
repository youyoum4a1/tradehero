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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class UserWatchlistPositionCache extends StraightDTOCache<UserBaseKey, SecurityIdList>
{
    private static final int DEFAULT_MAX_SIZE = 200;
    private static final int DEFAULT_WATCHLIST_FETCH_SIZE = 100;

    @NotNull protected Lazy<WatchlistServiceWrapper> watchlistServiceWrapper;
    @NotNull protected Lazy<WatchlistPositionCache> watchlistPositionCache;

    //<editor-fold desc="Constructors">
    @Inject public UserWatchlistPositionCache(
            @NotNull Lazy<WatchlistServiceWrapper> watchlistServiceWrapper,
            @NotNull Lazy<WatchlistPositionCache> watchlistPositionCache)
    {
        super(DEFAULT_MAX_SIZE);
        this.watchlistServiceWrapper = watchlistServiceWrapper;
        this.watchlistPositionCache = watchlistPositionCache;
    }
    //</editor-fold>

    // TODO change the cache to use SkipCacheSecurityPerPagedWatchlistKey in order to provide pagination
    protected PerPagedWatchlistKey createUniqueKey()
    {
        return new PerPagedWatchlistKey(1, DEFAULT_WATCHLIST_FETCH_SIZE);
    }

    @Override protected SecurityIdList fetch(@NotNull UserBaseKey key) throws Throwable
    {
        return putInternal(
                watchlistServiceWrapper.get().getAllByUser(createUniqueKey()));
    }

    @Contract("null -> null; !null -> !null") @Nullable
    private SecurityIdList putInternal(@Nullable List<WatchlistPositionDTO> watchlistPositionDTOs)
    {
        SecurityIdList securityIds = new SecurityIdList();
        if (watchlistPositionDTOs != null)
        {
            watchlistPositionCache.get().invalidateAll();
            for (@NotNull WatchlistPositionDTO watchlistPositionDTO : watchlistPositionDTOs)
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
