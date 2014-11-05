package com.tradehero.th.api.leaderboard;

import com.tradehero.th.api.leaderboard.key.LeaderboardUserId;
import com.tradehero.th.api.leaderboard.key.LeaderboardUserIdList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class LeaderboardUserDTOUtil
{
    //<editor-fold desc="Constructors">
    @Inject public LeaderboardUserDTOUtil()
    {
    }
    //</editor-fold>

    @Nullable
    public Map<LeaderboardUserId, LeaderboardUserDTO> map(@Nullable List<LeaderboardUserDTO> leaderboardUserDTOs)
    {
        if (leaderboardUserDTOs == null)
        {
            return null;
        }

        Map<LeaderboardUserId, LeaderboardUserDTO> returned = new HashMap<>();
        for (LeaderboardUserDTO leaderboardUserDTO: leaderboardUserDTOs)
        {
            returned.put(leaderboardUserDTO.getLeaderboardUserId(), leaderboardUserDTO);
        }
        return returned;
    }

    @Nullable
    public LeaderboardUserIdList getIds(@Nullable List<LeaderboardUserDTO> leaderboardUserDTOs)
    {
        if (leaderboardUserDTOs == null)
        {
            return null;
        }

        LeaderboardUserIdList returned = new LeaderboardUserIdList();
        for (LeaderboardUserDTO leaderboardUserDTO: leaderboardUserDTOs)
        {
            returned.add(leaderboardUserDTO.getLeaderboardUserId());
        }
        return returned;
    }
}
