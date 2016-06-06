package com.androidth.general.persistence.leaderboard;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.api.leaderboard.LeaderboardDTO;
import com.androidth.general.api.leaderboard.key.LeaderboardKey;
import com.androidth.general.network.service.LeaderboardServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class LeaderboardCacheRx extends BaseFetchDTOCacheRx<LeaderboardKey, LeaderboardDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    // We need to compose here, instead of inheritance, otherwise we get a compile error regarding erasure on put and put.
    @NonNull private final Lazy<LeaderboardUserCacheRx> leaderboardUserCache;
    @NonNull private final LeaderboardServiceWrapper leaderboardServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public LeaderboardCacheRx(
            @NonNull Lazy<LeaderboardUserCacheRx> leaderboardUserCache,
            @NonNull LeaderboardServiceWrapper leaderboardServiceWrapper,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_SIZE, dtoCacheUtil);
        this.leaderboardUserCache = leaderboardUserCache;
        this.leaderboardServiceWrapper = leaderboardServiceWrapper;
    }
    //</editor-fold>

    @Override @NonNull protected Observable<LeaderboardDTO> fetch(@NonNull LeaderboardKey key)
    {
        return leaderboardServiceWrapper.getLeaderboardRx(key);
    }

    @Override public void onNext(@NonNull LeaderboardKey key, @NonNull LeaderboardDTO value)
    {
        leaderboardUserCache.get().onNext(value.users);
        super.onNext(key, value);
    }
}
