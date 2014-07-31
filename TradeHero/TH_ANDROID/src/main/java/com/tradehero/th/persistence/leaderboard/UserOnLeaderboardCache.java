package com.tradehero.th.persistence.leaderboard;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.key.UserOnLeaderboardKey;
import com.tradehero.th.network.service.LeaderboardServiceWrapper;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class UserOnLeaderboardCache extends StraightDTOCacheNew<UserOnLeaderboardKey, LeaderboardDTO>
{
    public static final int DEFAULT_MAX_SIZE = 10;

    @NotNull private final LeaderboardServiceWrapper leaderboardServiceWrapper;

    @Inject public UserOnLeaderboardCache(LeaderboardServiceWrapper leaderboardServiceWrapper)
    {
        super(DEFAULT_MAX_SIZE);
        this.leaderboardServiceWrapper = leaderboardServiceWrapper;
    }

    @NotNull @Override public LeaderboardDTO fetch(@NotNull UserOnLeaderboardKey key) throws Throwable
    {
        return leaderboardServiceWrapper.getUserOnLeaderboard(key, null);
    }
}