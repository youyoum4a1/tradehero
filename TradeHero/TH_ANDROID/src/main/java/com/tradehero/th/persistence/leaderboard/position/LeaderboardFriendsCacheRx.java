package com.tradehero.th.persistence.leaderboard.position;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.th.api.leaderboard.position.LeaderboardFriendsDTO;
import com.tradehero.th.api.leaderboard.position.LeaderboardFriendsKey;
import com.tradehero.th.network.service.LeaderboardServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton
public class LeaderboardFriendsCacheRx extends BaseFetchDTOCacheRx<LeaderboardFriendsKey, LeaderboardFriendsDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1;

    @NotNull private final LeaderboardServiceWrapper leaderboardServiceWrapper;

    @Inject public LeaderboardFriendsCacheRx(@NotNull LeaderboardServiceWrapper leaderboardServiceWrapper)
    {
        super(DEFAULT_MAX_SIZE, DEFAULT_MAX_SIZE, DEFAULT_MAX_SIZE);
        this.leaderboardServiceWrapper = leaderboardServiceWrapper;
    }

    @NotNull @Override protected Observable<LeaderboardFriendsDTO> fetch(@NotNull LeaderboardFriendsKey key)
    {
        return leaderboardServiceWrapper.getNewFriendsLeaderboardRx();
    }
}
