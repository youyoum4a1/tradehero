package com.tradehero.th.persistence.leaderboard;

import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTOList;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;

@Singleton @UserCache
public class LeaderboardDefCache extends StraightDTOCacheNew<LeaderboardDefKey, LeaderboardDefDTO>
{
    private static final int DEFAULT_MAX_SIZE = 1000;

    @NonNull private final Lazy<LeaderboardDefListCache> leaderboardDefListCache;

    //<editor-fold desc="Constructors">
    @Inject public LeaderboardDefCache(
            @NonNull Lazy<LeaderboardDefListCache> leaderboardDefListCache,
            @NonNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(DEFAULT_MAX_SIZE, dtoCacheUtil);
        this.leaderboardDefListCache = leaderboardDefListCache;
    }
    //</editor-fold>

    @Override @NonNull public LeaderboardDefDTO fetch(@NonNull final LeaderboardDefKey key) throws Throwable
    {
        leaderboardDefListCache.get().getOrFetchSync(new LeaderboardDefListKey());
        LeaderboardDefDTO found = get(key);
        if (found != null)
        {
            return found;
        }
        throw new NullPointerException("No such leaderboardDef");
    }

    public LeaderboardDefDTOList get(List<LeaderboardDefKey> keys)
    {
        if (keys == null)
        {
            return null;
        }

        LeaderboardDefDTOList ret = new LeaderboardDefDTOList();
        for (LeaderboardDefKey key: keys)
        {
            ret.add(get(key));
        }
        return ret;
    }

    public void put(@NonNull LeaderboardDefDTOList leaderboardDefDTOs)
    {
        for (LeaderboardDefDTO leaderboardDefDTO: leaderboardDefDTOs)
        {
            LeaderboardDefKey key = leaderboardDefDTO.getLeaderboardDefKey();
            put(key, leaderboardDefDTO);
        }
    }
}
