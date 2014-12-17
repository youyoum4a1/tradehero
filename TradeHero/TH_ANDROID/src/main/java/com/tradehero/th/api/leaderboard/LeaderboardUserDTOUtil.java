package com.tradehero.th.api.leaderboard;

import android.support.annotation.NonNull;
import com.tradehero.th.api.leaderboard.key.LeaderboardUserId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

public class LeaderboardUserDTOUtil
{
    //<editor-fold desc="Constructors">
    @Inject public LeaderboardUserDTOUtil()
    {
    }
    //</editor-fold>

    @NonNull
    public Map<LeaderboardUserId, LeaderboardUserDTO> map(@NonNull List<LeaderboardUserDTO> leaderboardUserDTOs)
    {
        Map<LeaderboardUserId, LeaderboardUserDTO> returned = new HashMap<>();
        for (LeaderboardUserDTO leaderboardUserDTO : leaderboardUserDTOs)
        {
            returned.put(leaderboardUserDTO.getLeaderboardUserId(), leaderboardUserDTO);
        }
        return returned;
    }
}
