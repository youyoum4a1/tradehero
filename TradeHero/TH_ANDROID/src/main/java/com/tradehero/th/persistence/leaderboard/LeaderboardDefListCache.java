package com.tradehero.th.persistence.leaderboard;

import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTOFactory;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTOList;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefKeyList;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import com.tradehero.th.network.service.LeaderboardServiceWrapper;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class LeaderboardDefListCache extends StraightCutDTOCacheNew<LeaderboardDefListKey, LeaderboardDefDTOList, LeaderboardDefKeyList>
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

    @Override @NotNull public LeaderboardDefDTOList fetch(@NotNull LeaderboardDefListKey listKey) throws Throwable
    {
        LeaderboardDefDTOList received = leaderboardServiceWrapper.getLeaderboardDefinitions();
        put(leaderboardDefDTOFactory.file(received)); // We have to do it here to avoid an infinite loop
        return received;
    }

    @NotNull @Override protected LeaderboardDefKeyList cutValue(@NotNull LeaderboardDefListKey key, @NotNull LeaderboardDefDTOList value)
    {
        leaderboardDefCache.put(value);
        return value.createKeys();
    }

    @Nullable @Override protected LeaderboardDefDTOList inflateValue(@NotNull LeaderboardDefListKey key, @Nullable LeaderboardDefKeyList cutValue)
    {
        if (cutValue == null)
        {
            return null;
        }
        LeaderboardDefDTOList value = leaderboardDefCache.get(cutValue);
        if (value.hasNullItem())
        {
            return null;
        }
        return value;
    }

    public void put(@NotNull Map<LeaderboardDefListKey, LeaderboardDefDTOList> keyMap)
    {
        for (Map.Entry<LeaderboardDefListKey, LeaderboardDefDTOList> entry : keyMap.entrySet())
        {
            put(entry.getKey(), entry.getValue());
        }
    }
}
