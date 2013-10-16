package com.tradehero.th.persistence.leaderboard;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDefKey;
import com.tradehero.th.api.leaderboard.LeaderboardDefListKey;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: tho Date: 10/16/13 Time: 12:52 PM Copyright (c) TradeHero */
@Singleton
public class LeaderboardDefCache extends StraightDTOCache<Integer, LeaderboardDefKey, LeaderboardDefDTO>
{
    private static final int DEFAULT_MAX_SIZE = 1000;

    @Inject protected Lazy<LeaderboardDefListCache> leaderboardDefListCache;

    public LeaderboardDefCache()
    {
        super(DEFAULT_MAX_SIZE);
    }

    @Override protected LeaderboardDefDTO fetch(LeaderboardDefKey key)
    {
        leaderboardDefListCache.get().fetch(new LeaderboardDefListKey(LeaderboardDefListKey.ALL_LEADERBOARD_DEF));
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
}
