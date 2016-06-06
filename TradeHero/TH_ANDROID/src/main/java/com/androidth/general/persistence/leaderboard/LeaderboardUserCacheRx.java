package com.androidth.general.persistence.leaderboard;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.api.leaderboard.LeaderboardUserDTO;
import com.androidth.general.api.leaderboard.key.LeaderboardUserId;
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

    public void onNext(@NonNull List<? extends LeaderboardUserDTO> leaderboardUserDTOs)
    {
        for (LeaderboardUserDTO leaderboardUserDTO : leaderboardUserDTOs)
        {
            onNext(leaderboardUserDTO.getLeaderboardUserId(), leaderboardUserDTO);
        }
    }
}
