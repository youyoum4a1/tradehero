package com.tradehero.th.persistence.leaderboard;

import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTOFactory;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTOList;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefKeyList;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import com.tradehero.th.network.service.LeaderboardServiceWrapper;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@Singleton @UserCache
public class LeaderboardDefListCache extends StraightCutDTOCacheNew<LeaderboardDefListKey, LeaderboardDefDTOList, LeaderboardDefKeyList>
{
    private static final int DEFAULT_MAX_SIZE = 1000;

    @NonNull private final LeaderboardServiceWrapper leaderboardServiceWrapper;
    @NonNull private final LeaderboardDefCache leaderboardDefCache;
    @NonNull private final LeaderboardDefDTOFactory leaderboardDefDTOFactory;

    //<editor-fold desc="Constructors">
    @Inject public LeaderboardDefListCache(
            @NonNull LeaderboardServiceWrapper leaderboardServiceWrapper,
            @NonNull LeaderboardDefCache leaderboardDefCache,
            @NonNull LeaderboardDefDTOFactory leaderboardDefDTOFactory,
            @NonNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(DEFAULT_MAX_SIZE, dtoCacheUtil);
        this.leaderboardServiceWrapper = leaderboardServiceWrapper;
        this.leaderboardDefCache = leaderboardDefCache;
        this.leaderboardDefDTOFactory =leaderboardDefDTOFactory;
    }
    //</editor-fold>

    @Override @NonNull public LeaderboardDefDTOList fetch(@NonNull LeaderboardDefListKey listKey) throws Throwable
    {
        LeaderboardDefDTOList received = leaderboardServiceWrapper.getLeaderboardDefinitions();
        put(leaderboardDefDTOFactory.file(received)); // We have to do it here to avoid an infinite loop
        return received;
    }

    @NonNull @Override protected LeaderboardDefKeyList cutValue(@NonNull LeaderboardDefListKey key, @NonNull LeaderboardDefDTOList value)
    {
        leaderboardDefCache.put(value);
        return value.createKeys();
    }

    @Nullable @Override protected LeaderboardDefDTOList inflateValue(@NonNull LeaderboardDefListKey key, @Nullable LeaderboardDefKeyList cutValue)
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

    public void put(@NonNull Map<LeaderboardDefListKey, LeaderboardDefDTOList> keyMap)
    {
        for (Map.Entry<LeaderboardDefListKey, LeaderboardDefDTOList> entry : keyMap.entrySet())
        {
            put(entry.getKey(), entry.getValue());
        }
    }
}
