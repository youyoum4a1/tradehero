package com.tradehero.th.persistence.leaderboard;

import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTOList;
import com.tradehero.th.api.leaderboard.key.LeaderboardUserId;
import com.tradehero.th.api.leaderboard.key.LeaderboardUserIdList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@Singleton @UserCache @Deprecated
public class LeaderboardUserCache extends StraightDTOCacheNew<LeaderboardUserId, LeaderboardUserDTO>
{
    private static final int DEFAULT_MAX_SIZE = 1000;

    @Inject public LeaderboardUserCache(@NonNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(DEFAULT_MAX_SIZE, dtoCacheUtil);
    }

    @Override @NonNull public LeaderboardUserDTO fetch(@NonNull LeaderboardUserId key) throws Throwable
    {
        throw new IllegalStateException("There is no fetch on LeaderboardUserCache");
    }

    public void put(@Nullable Map<LeaderboardUserId, LeaderboardUserDTO> leaderboardUserDTOs)
    {
        if (leaderboardUserDTOs == null)
        {
            return;
        }

        for (Map.Entry<LeaderboardUserId, LeaderboardUserDTO> pair: leaderboardUserDTOs.entrySet())
        {
            put(pair.getKey(), pair.getValue());
        }
    }

    @Nullable
    public LeaderboardUserDTOList get(@Nullable List<LeaderboardUserId> leaderboardUserIds)
    {
        if (leaderboardUserIds == null)
        {
            return null;
        }

        LeaderboardUserDTOList returned = new LeaderboardUserDTOList();
        for (LeaderboardUserId leaderboardUserId: leaderboardUserIds)
        {
            returned.add(get(leaderboardUserId));
        }
        return returned;
    }

    @Nullable
    public LeaderboardUserDTOList put(@Nullable List<? extends LeaderboardUserDTO> leaderboardUserDTOs)
    {
        if (leaderboardUserDTOs == null)
        {
            return null;
        }
        LeaderboardUserDTOList previous = new LeaderboardUserDTOList();
        for (LeaderboardUserDTO leaderboardUserDTO : leaderboardUserDTOs)
        {
            previous.add(put(leaderboardUserDTO.getLeaderboardUserId(), leaderboardUserDTO));
        }
        return previous;
    }

    public LeaderboardUserIdList getAllKeys()
    {
        return new LeaderboardUserIdList(snapshot().keySet(), (LeaderboardUserId) null);
    }
}
