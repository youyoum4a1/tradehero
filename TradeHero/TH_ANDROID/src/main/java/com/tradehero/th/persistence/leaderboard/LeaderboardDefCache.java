package com.tradehero.th.persistence.leaderboard;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTOList;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Singleton public class LeaderboardDefCache extends StraightDTOCacheNew<LeaderboardDefKey, LeaderboardDefDTO>
{
    private static final int DEFAULT_MAX_SIZE = 1000;

    //<editor-fold desc="Constructors">
    @Inject public LeaderboardDefCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override @NotNull public LeaderboardDefDTO fetch(@NotNull LeaderboardDefKey key) throws Throwable
    {
        throw new IllegalStateException("Cannot fetch on this cache");
    }

    @Contract("null -> null; !null -> !null")
    public LeaderboardDefDTOList get(List<LeaderboardDefKey> keys)
    {
        if (keys == null)
        {
            return null;
        }

        LeaderboardDefDTOList ret = new LeaderboardDefDTOList();
        for (@NotNull LeaderboardDefKey key: keys)
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
