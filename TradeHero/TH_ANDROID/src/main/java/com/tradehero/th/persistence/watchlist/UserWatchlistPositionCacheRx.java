package com.tradehero.th.persistence.watchlist;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import com.tradehero.th.api.watchlist.key.PerPagedWatchlistKey;
import com.tradehero.th.network.service.WatchlistServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache public class UserWatchlistPositionCacheRx extends BaseFetchDTOCacheRx<UserBaseKey, WatchlistPositionDTOList>
{
    private static final int DEFAULT_MAX_SIZE = 200;
    private static final int DEFAULT_WATCHLIST_FETCH_SIZE = 100;

    @NonNull protected final Lazy<WatchlistServiceWrapper> watchlistServiceWrapper;
    @NonNull protected final Lazy<WatchlistPositionCacheRx> watchlistPositionCache;

    //<editor-fold desc="Constructors">
    @Inject public UserWatchlistPositionCacheRx(
            @NonNull Lazy<WatchlistServiceWrapper> watchlistServiceWrapper,
            @NonNull Lazy<WatchlistPositionCacheRx> watchlistPositionCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_SIZE, dtoCacheUtil);
        this.watchlistServiceWrapper = watchlistServiceWrapper;
        this.watchlistPositionCache = watchlistPositionCache;
    }
    //</editor-fold>

    // TODO change the cache to use SkipCacheSecurityPerPagedWatchlistKey in order to provide pagination
    protected PerPagedWatchlistKey createUniqueKey()
    {
        return new PerPagedWatchlistKey(1, DEFAULT_WATCHLIST_FETCH_SIZE);
    }

    @Override @NonNull protected Observable<WatchlistPositionDTOList> fetch(@NonNull UserBaseKey key)
    {
        return watchlistServiceWrapper.get().getAllByUserRx(createUniqueKey());
    }

    @Override public void onNext(@NonNull UserBaseKey key, @NonNull WatchlistPositionDTOList value)
    {
        watchlistPositionCache.get().onNext(value);
        super.onNext(key, value);
    }
}
