package com.tradehero.th.persistence.leaderboard;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTOList;
import com.tradehero.th.api.users.SuggestHeroesListType;
import com.tradehero.th.network.service.UserServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class LeaderboardUserListCacheRx
        extends BaseFetchDTOCacheRx<SuggestHeroesListType, LeaderboardUserDTOList>
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

    @NonNull @Override protected Observable<LeaderboardUserDTOList> fetch(@NonNull SuggestHeroesListType key)
    {
        return userServiceWrapper.suggestHeroesRx(key);
    }

    @Override public void onNext(@NonNull SuggestHeroesListType key, @NonNull LeaderboardUserDTOList value)
    {
        leaderboardUserCache.get().put(value);
        super.onNext(key, value);
    }
}
