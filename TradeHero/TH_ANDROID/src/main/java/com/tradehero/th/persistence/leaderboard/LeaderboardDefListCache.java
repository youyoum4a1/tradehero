package com.tradehero.th.persistence.leaderboard;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDefKeyList;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import com.tradehero.th.network.service.LeaderboardService;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: tho Date: 10/16/13 Time: 10:32 AM Copyright (c) TradeHero */
@Singleton public class LeaderboardDefListCache extends StraightDTOCache<LeaderboardDefListKey, LeaderboardDefKeyList>
{
    public static final String TAG = LeaderboardDefListCache.class.getSimpleName();
    private static final int DEFAULT_MAX_SIZE = 1000;

    @Inject protected Lazy<LeaderboardService> leaderboardService;
    @Inject protected Lazy<LeaderboardDefCache> leaderboardDefCache;

    @Inject public LeaderboardDefListCache()
    {
        super(DEFAULT_MAX_SIZE);
    }

    @Override protected LeaderboardDefKeyList fetch(LeaderboardDefListKey listKey) throws Throwable
    {
        List<LeaderboardDefDTO> leaderboardDefinitions = leaderboardService.get().getLeaderboardDefinitions();
        if (leaderboardDefinitions != null)
        {
            return putInternal(listKey, leaderboardDefinitions);
        }
        else
        {
            return null;
        }
    }

    private LeaderboardDefKeyList putInternal(LeaderboardDefListKey listKey, List<LeaderboardDefDTO> allLeaderboardDefinitions)
    {
        LeaderboardDefKeyList
                allKeys = new LeaderboardDefKeyList(),
                sectorKeys = new LeaderboardDefKeyList(),
                exchangeKeys = new LeaderboardDefKeyList(),
                timePeriodKeys = new LeaderboardDefKeyList(),
                mostSkilledKeys = new LeaderboardDefKeyList();

        for (LeaderboardDefDTO leaderboardDefDTO: allLeaderboardDefinitions)
        {
            LeaderboardDefKey key = new LeaderboardDefKey(leaderboardDefDTO.id);
            leaderboardDefCache.get().put(key, leaderboardDefDTO);

            allKeys.add(key);
            if (leaderboardDefDTO.exchangeRestrictions)
            {
                exchangeKeys.add(key);
            }
            else if (leaderboardDefDTO.sectorRestrictions)
            {
                sectorKeys.add(key);
            }
            else if (leaderboardDefDTO.isTimeRestrictedLeaderboard())
            {
                timePeriodKeys.add(key);
            }
            else if (leaderboardDefDTO.id == LeaderboardDefDTO.LEADERBOARD_DEF_MOST_SKILLED_ID)
            {
                mostSkilledKeys.add(key);
            }
        }

        put(LeaderboardDefListKey.getMostSkilled(), mostSkilledKeys);
        put(LeaderboardDefListKey.getExchange(), exchangeKeys);
        put(LeaderboardDefListKey.getSector(), sectorKeys);
        put(LeaderboardDefListKey.getTimePeriod(), timePeriodKeys);
        put(new LeaderboardDefListKey(), allKeys);

        return get(listKey);
    }
}
