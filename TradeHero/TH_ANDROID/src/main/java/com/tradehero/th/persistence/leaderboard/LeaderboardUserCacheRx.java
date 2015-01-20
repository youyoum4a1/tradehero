package com.tradehero.th.persistence.leaderboard;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.key.LeaderboardUserId;
import com.tradehero.th.api.leaderboard.key.LeaderboardUserIdList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @UserCache
public class LeaderboardUserCacheRx extends BaseDTOCacheRx<LeaderboardUserId, LeaderboardUserDTO>
{
    private static final int DEFAULT_MAX_VALUE_SIZE = 1000;

    //<editor-fold desc="Constructors">
    @Inject public LeaderboardUserCacheRx(@NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
    }
    //</editor-fold>

    public void onNext(@NonNull Map<LeaderboardUserId, LeaderboardUserDTO> leaderboardUserDTOs)
    {
        for (Map.Entry<LeaderboardUserId, LeaderboardUserDTO> pair: leaderboardUserDTOs.entrySet())
        {
            onNext(pair.getKey(), pair.getValue());
        }
    }

    public void put(@NonNull List<? extends LeaderboardUserDTO> leaderboardUserDTOs)
    {
        for (LeaderboardUserDTO leaderboardUserDTO : leaderboardUserDTOs)
        {
            onNext(leaderboardUserDTO.getLeaderboardUserId(), leaderboardUserDTO);
        }
    }

    public LeaderboardUserIdList getAllKeys()
    {
        return new LeaderboardUserIdList(snapshot().keySet(), (LeaderboardUserId) null);
    }
}
