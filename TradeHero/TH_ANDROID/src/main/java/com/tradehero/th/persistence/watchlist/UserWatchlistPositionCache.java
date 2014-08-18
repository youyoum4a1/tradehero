package com.tradehero.th.persistence.watchlist;

import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import com.tradehero.th.api.watchlist.key.PerPagedWatchlistKey;
import com.tradehero.th.network.service.WatchlistServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class UserWatchlistPositionCache extends StraightCutDTOCacheNew<UserBaseKey, WatchlistPositionDTOList, SecurityIdList>
{
    private static final int DEFAULT_MAX_SIZE = 200;
    private static final int DEFAULT_WATCHLIST_FETCH_SIZE = 100;

    @NotNull protected final Lazy<WatchlistServiceWrapper> watchlistServiceWrapper;
    @NotNull protected final Lazy<WatchlistPositionCache> watchlistPositionCache;

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

    @Override @NotNull public WatchlistPositionDTOList fetch(@NotNull UserBaseKey key) throws Throwable
    {
        return watchlistServiceWrapper.get().getAllByUser(createUniqueKey());
    }

    @NotNull @Override protected SecurityIdList cutValue(@NotNull UserBaseKey key, @NotNull WatchlistPositionDTOList value)
    {
        watchlistPositionCache.get().put(value);
        return value.getSecurityIds();
    }

    @Nullable @Override protected WatchlistPositionDTOList inflateValue(@NotNull UserBaseKey key, @Nullable SecurityIdList cutValue)
    {
        if (cutValue == null)
        {
            return null;
        }
        WatchlistPositionDTOList inflated = watchlistPositionCache.get().get(cutValue);
        if (inflated.hasNullItem())
        {
            return null;
        }
        return inflated;
    }
}
