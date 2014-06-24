package com.tradehero.th.persistence.leaderboard;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTOList;
import com.tradehero.th.api.leaderboard.key.LeaderboardUserId;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    @Contract("null -> null; !null -> !null")
    public void put(@Nullable Map<LeaderboardUserId, LeaderboardUserDTO> leaderboardUserDTOs)
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

    @Contract("null -> null; !null -> !null")
    public LeaderboardUserDTOList get(List<LeaderboardUserId> leaderboardUserIds)
    {
        if (leaderboardUserIds == null)
        {
            return null;
        }

        LeaderboardUserDTOList  returned = new LeaderboardUserDTOList();
        for (@NotNull LeaderboardUserId leaderboardUserId: leaderboardUserIds)
        {
            returned.add(get(leaderboardUserId));
        }
        return returned;
    }
}
