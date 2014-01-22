package com.tradehero.th.persistence.leaderboard;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class LeaderboardUserCache extends StraightDTOCache<LeaderboardUserId, LeaderboardUserDTO>
{
    private static final int DEFAULT_MAX_SIZE = 1000;

    @Inject public LeaderboardUserCache()
    {
        super(DEFAULT_MAX_SIZE);
    }

    @Override protected LeaderboardUserDTO fetch(LeaderboardUserId key) throws Throwable
    {
        throw new IllegalStateException("There is no fetch on LeaderboardUserCache");
    }

    public void put(Map<LeaderboardUserId, LeaderboardUserDTO> leaderboardUserDTOs)
    {
        if (leaderboardUserDTOs == null)
        {
            return;
        }

        for (Map.Entry<LeaderboardUserId, LeaderboardUserDTO> pair: leaderboardUserDTOs.entrySet())
        {
            put(pair.getKey(), pair.getValue());
        }
    }

    public List<LeaderboardUserDTO> get(List<LeaderboardUserId> leaderboardUserIds)
    {
        if (leaderboardUserIds == null)
        {
            return null;
        }

        List<LeaderboardUserDTO> returned = new ArrayList<>();
        for (LeaderboardUserId leaderboardUserId: leaderboardUserIds)
        {
            returned.add(get(leaderboardUserId));
        }
        return returned;
    }
}
