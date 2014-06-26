package com.tradehero.th.persistence.leaderboard.position;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.leaderboard.position.LeaderboardFriendsDTO;
import com.tradehero.th.api.leaderboard.position.LeaderboardFriendsKey;
import com.tradehero.th.network.service.LeaderboardServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton
public class LeaderboardFriendsCache extends StraightDTOCacheNew<LeaderboardFriendsKey, LeaderboardFriendsDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1;

    @NotNull private final LeaderboardServiceWrapper leaderboardServiceWrapper;

    @Inject public LeaderboardFriendsCache(LeaderboardServiceWrapper leaderboardServiceWrapper)
    {
        super(DEFAULT_MAX_SIZE);
        this.leaderboardServiceWrapper = leaderboardServiceWrapper;
    }

    @Override public LeaderboardFriendsDTO fetch(@NotNull LeaderboardFriendsKey key) throws Throwable
    {
        return leaderboardServiceWrapper.getNewFriendsLeaderboard();
    }

}
