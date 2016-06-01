package com.ayondo.academy.persistence.leaderboard;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.api.leaderboard.def.LeaderboardDefDTOList;
import com.ayondo.academy.api.leaderboard.key.LeaderboardDefListKey;
import com.ayondo.academy.network.service.LeaderboardServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class LeaderboardDefListCacheRx extends BaseFetchDTOCacheRx<LeaderboardDefListKey, LeaderboardDefDTOList>
{
    private static final int DEFAULT_MAX_SIZE = 1000;

    @NonNull private final LeaderboardServiceWrapper leaderboardServiceWrapper;
    @NonNull private final LeaderboardDefCacheRx leaderboardDefCache;

    //<editor-fold desc="Constructors">
    @Inject public LeaderboardDefListCacheRx(
            @NonNull LeaderboardServiceWrapper leaderboardServiceWrapper,
            @NonNull LeaderboardDefCacheRx leaderboardDefCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_SIZE, dtoCacheUtil);
        this.leaderboardServiceWrapper = leaderboardServiceWrapper;
        this.leaderboardDefCache = leaderboardDefCache;
    }
    //</editor-fold>

    @Override @NonNull protected Observable<LeaderboardDefDTOList> fetch(@NonNull final LeaderboardDefListKey listKey)
    {
        if (listKey.page != null && listKey.page > 1)
        {
            return Observable.just(new LeaderboardDefDTOList());
        }
        return leaderboardServiceWrapper.getLeaderboardDefinitionsRx();
    }

    @Override public void onNext(@NonNull LeaderboardDefListKey key, @NonNull LeaderboardDefDTOList value)
    {
        leaderboardDefCache.onNext(value);
        super.onNext(key, value);
    }
}
