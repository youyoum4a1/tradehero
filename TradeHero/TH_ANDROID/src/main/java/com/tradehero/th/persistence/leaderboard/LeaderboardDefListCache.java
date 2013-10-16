package com.tradehero.th.persistence.leaderboard;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDefKey;
import com.tradehero.th.api.leaderboard.LeaderboardDefListKey;
import com.tradehero.th.network.service.LeaderboardService;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: tho Date: 10/16/13 Time: 10:32 AM Copyright (c) TradeHero */
@Singleton
public class LeaderboardDefListCache extends StraightDTOCache<Integer, LeaderboardDefListKey, List<LeaderboardDefKey>>
{
    private static final int DEFAULT_MAX_SIZE = 1000;

    @Inject protected Lazy<LeaderboardService> leaderboardService;
    @Inject protected Lazy<LeaderboardDefCache> leaderboardDefCache;

    public LeaderboardDefListCache()
    {
        super(DEFAULT_MAX_SIZE);
    }

    @Override protected List<LeaderboardDefKey> fetch(LeaderboardDefListKey key)
    {
        if (LeaderboardDefListKey.ALL_LEADERBOARD_DEF.compareTo(key.key) == 0)
        {
            // request for all LeaderboardDefDTO
            return putInternal(leaderboardService.get().getLeaderboardDefinitions());
        }

        return null;
    }

    private List<LeaderboardDefKey> putInternal(List<LeaderboardDefDTO> allLeaderboardDefinitions)
    {
        List<LeaderboardDefKey> allKeys = new ArrayList<>();

        for (LeaderboardDefDTO leaderboardDefDTO: allLeaderboardDefinitions)
        {
            LeaderboardDefKey key = new LeaderboardDefKey(leaderboardDefDTO.id);
            allKeys.add(key);
            leaderboardDefCache.get().put(new LeaderboardDefKey(leaderboardDefDTO.id), leaderboardDefDTO);
        }
        return allKeys;
    }
}
