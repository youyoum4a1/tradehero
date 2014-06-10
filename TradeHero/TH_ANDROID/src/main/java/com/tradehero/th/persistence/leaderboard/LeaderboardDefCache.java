package com.tradehero.th.persistence.leaderboard;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTOList;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

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
        // if leaderboardDef is not in the cache, request for all lbdef again to refresh the cache
        // TODO leaderboardDefListCache.get().fetch(key);
        return get(key);
    }

    public LeaderboardDefDTOList getOrFetch(List<LeaderboardDefKey> keys) throws Throwable
    {
        if (keys == null)
        {
            return null;
        }

        LeaderboardDefDTOList ret = new LeaderboardDefDTOList();
        for (LeaderboardDefKey key: keys)
        {
            ret.add(getOrFetch(key, false));
        }
        return ret;
    }

    @Override public LeaderboardDefDTO put(LeaderboardDefKey key, LeaderboardDefDTO value)
    {
        return super.put(key, value);
    }
}
