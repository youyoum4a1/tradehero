package com.androidth.general.persistence.leaderboard;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.api.leaderboard.LeaderboardUserDTOList;
import com.androidth.general.api.users.UserListType;
import com.androidth.general.network.service.UserServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class LeaderboardUserListCacheRx
        extends BaseFetchDTOCacheRx<UserListType, LeaderboardUserDTOList>
{
    private static final int MAX_SIZE = 10;

    @NonNull private final Lazy<LeaderboardUserCacheRx> leaderboardUserCache;
    @NonNull private final UserServiceWrapper userServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public LeaderboardUserListCacheRx(
            @NonNull Lazy<LeaderboardUserCacheRx> leaderboardUserCache,
            @NonNull UserServiceWrapper userServiceWrapper,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(MAX_SIZE, dtoCacheUtil);
        this.leaderboardUserCache = leaderboardUserCache;
        this.userServiceWrapper = userServiceWrapper;
    }
    //</editor-fold>

    @NonNull @Override protected Observable<LeaderboardUserDTOList> fetch(@NonNull UserListType key)
    {
        return userServiceWrapper.suggestHeroesRx(key);
    }

    @Override public void onNext(@NonNull UserListType key, @NonNull LeaderboardUserDTOList value)
    {
        leaderboardUserCache.get().onNext(value);
        super.onNext(key, value);
    }
}
