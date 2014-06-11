package com.tradehero.th.persistence.leaderboard;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.leaderboard.def.ConnectedLeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.def.DrillDownLeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefKeyList;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKeyKnowledge;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import com.tradehero.th.network.service.LeaderboardServiceWrapper;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class LeaderboardDefListCache extends StraightDTOCache<LeaderboardDefListKey, LeaderboardDefKeyList>
{
    private static final int DEFAULT_MAX_SIZE = 1000;

    @Inject protected Lazy<LeaderboardServiceWrapper> leaderboardServiceWrapper;
    @Inject protected Lazy<LeaderboardDefCache> leaderboardDefCache;

    @Inject public LeaderboardDefListCache()
    {
        super(DEFAULT_MAX_SIZE);
    }

    @Override protected LeaderboardDefKeyList fetch(LeaderboardDefListKey listKey) throws Throwable
    {
        List<LeaderboardDefDTO> leaderboardDefinitions = leaderboardServiceWrapper.get().getLeaderboardDefinitions();
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
                connectedKeys = new LeaderboardDefKeyList(),
                drillDownKeys = new LeaderboardDefKeyList(),
                sectorKeys = new LeaderboardDefKeyList(),
                exchangeKeys = new LeaderboardDefKeyList(),
                timePeriodKeys = new LeaderboardDefKeyList(),
                mostSkilledKeys = new LeaderboardDefKeyList();

        for (LeaderboardDefDTO leaderboardDefDTO: allLeaderboardDefinitions)
        {
            LeaderboardDefKey key = leaderboardDefDTO.getLeaderboardDefKey();
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
            else if (leaderboardDefDTO.id == LeaderboardDefKeyKnowledge.MOST_SKILLED_ID)
            {
                mostSkilledKeys.add(key);
            }
            else if (leaderboardDefDTO instanceof DrillDownLeaderboardDefDTO)
            {
                drillDownKeys.add(key);
            }
            else if (leaderboardDefDTO instanceof ConnectedLeaderboardDefDTO)
            {
                connectedKeys.add(key);
            }
        }

        put(LeaderboardDefListKey.getMostSkilled(), mostSkilledKeys);
        put(LeaderboardDefListKey.getExchange(), exchangeKeys);
        put(LeaderboardDefListKey.getSector(), sectorKeys);
        put(LeaderboardDefListKey.getTimePeriod(), timePeriodKeys);
        put(LeaderboardDefListKey.getConnected(), connectedKeys);
        put(LeaderboardDefListKey.getDrillDown(), drillDownKeys);
        put(new LeaderboardDefListKey(), allKeys);

        return get(listKey);
    }
}
