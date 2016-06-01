package com.ayondo.academy.persistence.leaderboard.position;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.api.leaderboard.position.LeaderboardFriendsDTO;
import com.ayondo.academy.api.leaderboard.position.LeaderboardFriendsKey;
import com.ayondo.academy.network.service.LeaderboardServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class LeaderboardFriendsCacheRx extends BaseFetchDTOCacheRx<LeaderboardFriendsKey, LeaderboardFriendsDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1;

    @NonNull private final LeaderboardServiceWrapper leaderboardServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public LeaderboardFriendsCacheRx(
            @NonNull LeaderboardServiceWrapper leaderboardServiceWrapper,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_SIZE, dtoCacheUtil);
        this.leaderboardServiceWrapper = leaderboardServiceWrapper;
    }
    //</editor-fold>

    @NonNull @Override protected Observable<LeaderboardFriendsDTO> fetch(@NonNull LeaderboardFriendsKey key)
    {
        return leaderboardServiceWrapper.getNewFriendsLeaderboardRx(key);
    }
}
