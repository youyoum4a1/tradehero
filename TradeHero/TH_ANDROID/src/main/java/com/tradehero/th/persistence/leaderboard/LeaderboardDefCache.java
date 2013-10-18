package com.tradehero.th.persistence.leaderboard;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDefKey;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: tho Date: 10/16/13 Time: 12:52 PM Copyright (c) TradeHero */
@Singleton public class LeaderboardDefCache extends StraightDTOCache<Integer, LeaderboardDefKey, LeaderboardDefDTO>
{
    private static final int DEFAULT_MAX_SIZE = 1000;

    @Inject protected Lazy<LeaderboardDefCache> leaderboardDefListCache;

    @Inject public LeaderboardDefCache()
    {
        super(DEFAULT_MAX_SIZE);
    }

    @Override protected LeaderboardDefDTO fetch(LeaderboardDefKey key)
    {
        // if leaderboardDef is not in the cache, request for all lbdef again to refresh the cache
        leaderboardDefListCache.get().fetch(key);
        return get(key);
    }

    public List<LeaderboardDefDTO> getOrFetch(List<LeaderboardDefKey> keys)
    {
        if (keys == null) {
            return null;
        }

        List<LeaderboardDefDTO> ret = new ArrayList<>();
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
