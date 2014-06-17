package com.tradehero.th.persistence.leaderboard;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTOFactory;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTOList;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefKeyList;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import com.tradehero.th.network.service.LeaderboardServiceWrapper;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class LeaderboardDefListCache extends StraightDTOCache<LeaderboardDefListKey, LeaderboardDefKeyList>
{
    private static final int DEFAULT_MAX_SIZE = 1000;

    @NotNull private final LeaderboardServiceWrapper leaderboardServiceWrapper;
    @NotNull private final LeaderboardDefCache leaderboardDefCache;
    @NotNull private final LeaderboardDefDTOFactory leaderboardDefDTOFactory;

    //<editor-fold desc="Constructors">
    @Inject public LeaderboardDefListCache(
            @NotNull LeaderboardServiceWrapper leaderboardServiceWrapper,
            @NotNull LeaderboardDefCache leaderboardDefCache,
            @NotNull LeaderboardDefDTOFactory leaderboardDefDTOFactory)
    {
        super(DEFAULT_MAX_SIZE);
        this.leaderboardServiceWrapper = leaderboardServiceWrapper;
        this.leaderboardDefCache = leaderboardDefCache;
        this.leaderboardDefDTOFactory =leaderboardDefDTOFactory;
    }
    //</editor-fold>

    @Override protected LeaderboardDefKeyList fetch(@NotNull LeaderboardDefListKey listKey) throws Throwable
    {
        LeaderboardDefDTOList leaderboardDefinitions = leaderboardServiceWrapper.getLeaderboardDefinitions();
        if (leaderboardDefinitions != null)
        {
            return putInternal(listKey, leaderboardDefinitions);
        }
        else
        {
            return null;
        }
    }

    private LeaderboardDefKeyList putInternal(
            @NotNull LeaderboardDefListKey listKey,
            @NotNull LeaderboardDefDTOList allLeaderboardDefinitions)
    {
        put(leaderboardDefDTOFactory.file(allLeaderboardDefinitions));
        leaderboardDefCache.put(allLeaderboardDefinitions);
        return get(listKey);
    }

    public void put(@NotNull Map<LeaderboardDefListKey, LeaderboardDefKeyList> keyMap)
    {
        for (Map.Entry<LeaderboardDefListKey, LeaderboardDefKeyList> entry : keyMap.entrySet())
        {
            put(entry.getKey(), entry.getValue());
        }
    }
}
