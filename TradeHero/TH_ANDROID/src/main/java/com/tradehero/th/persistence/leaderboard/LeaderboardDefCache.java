package com.tradehero.th.persistence.leaderboard;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTOList;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class LeaderboardDefCache extends StraightDTOCache<LeaderboardDefKey, LeaderboardDefDTO>
{
    private static final int DEFAULT_MAX_SIZE = 1000;

    @Inject protected Lazy<LeaderboardDefListCache> leaderboardDefListCache;

    @Inject public LeaderboardDefCache()
    {
        super(DEFAULT_MAX_SIZE);
    }

    @Override protected LeaderboardDefDTO fetch(LeaderboardDefKey key) throws Throwable
    {
        throw new IllegalStateException("Cannot fetch on this cache");
    }

    public LeaderboardDefDTOList get(List<LeaderboardDefKey> keys) throws Throwable
    {
        if (keys == null)
        {
            return null;
        }

        LeaderboardDefDTOList ret = new LeaderboardDefDTOList();
        for (LeaderboardDefKey key: keys)
        {
            ret.add(get(key));
        }
        return ret;
    }

    public void put(@NotNull LeaderboardDefDTOList leaderboardDefDTOs)
    {
        for (@NotNull LeaderboardDefDTO leaderboardDefDTO: leaderboardDefDTOs)
        {
            LeaderboardDefKey key = leaderboardDefDTO.getLeaderboardDefKey();
            put(key, leaderboardDefDTO);
        }
    }
}
