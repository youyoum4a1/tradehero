package com.androidth.general.persistence.watchlist;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.watchlist.WatchlistPositionDTOList;
import com.androidth.general.api.watchlist.key.PerPagedWatchlistKey;
import com.androidth.general.network.service.WatchlistServiceWrapper;
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
