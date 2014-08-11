package com.tradehero.th.persistence.leaderboard;

import com.android.internal.util.Predicate;
import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTOList;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Singleton public class LeaderboardDefCache extends StraightDTOCacheNew<LeaderboardDefKey, LeaderboardDefDTO>
{
    private static final int DEFAULT_MAX_SIZE = 1000;

    @NotNull private final Lazy<LeaderboardDefListCache> leaderboardDefListCache;

    //<editor-fold desc="Constructors">
    @Inject public LeaderboardDefCache(@NotNull Lazy<LeaderboardDefListCache> leaderboardDefListCache)
    {
        super(DEFAULT_MAX_SIZE);
        this.leaderboardDefListCache = leaderboardDefListCache;
    }
    //</editor-fold>

    @Override @NotNull public LeaderboardDefDTO fetch(@NotNull final LeaderboardDefKey key) throws Throwable
    {
        LeaderboardDefDTO found = leaderboardDefListCache.get().getOrFetchSync(
                new LeaderboardDefListKey()).findFirstWhere(new Predicate<LeaderboardDefDTO>()
        {
            @Override public boolean apply(LeaderboardDefDTO leaderboardDefDTO)
            {
                return leaderboardDefDTO.getLeaderboardDefKey().equals(key);
            }
        });
        if (found != null)
        {
            return found;
        }
        throw new NullPointerException("No such leaderboardDef");
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
