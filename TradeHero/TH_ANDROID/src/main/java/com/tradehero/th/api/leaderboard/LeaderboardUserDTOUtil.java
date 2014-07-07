package com.tradehero.th.api.leaderboard;

import com.tradehero.th.api.leaderboard.key.LeaderboardUserId;
import com.tradehero.th.api.leaderboard.key.LeaderboardUserIdList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LeaderboardUserDTOUtil
{
    //<editor-fold desc="Constructors">
    @Inject public LeaderboardUserDTOUtil()
    {
    }
    //</editor-fold>

    @Contract("null -> null; !null -> !null") @Nullable
    public Map<LeaderboardUserId, LeaderboardUserDTO> map(@Nullable List<LeaderboardUserDTO> leaderboardUserDTOs)
    {
        if (leaderboardUserDTOs == null)
        {
            return null;
        }

        Map<LeaderboardUserId, LeaderboardUserDTO> returned = new HashMap<>();
        for (@NotNull LeaderboardUserDTO leaderboardUserDTO: leaderboardUserDTOs)
        {
            returned.put(leaderboardUserDTO.getLeaderboardUserId(), leaderboardUserDTO);
        }
        return returned;
    }

    @Contract("null -> null; !null -> !null") @Nullable
    public LeaderboardUserIdList getIds(@Nullable List<LeaderboardUserDTO> leaderboardUserDTOs)
    {
        if (leaderboardUserDTOs == null)
        {
            return null;
        }

        LeaderboardUserIdList returned = new LeaderboardUserIdList();
        for (@NotNull LeaderboardUserDTO leaderboardUserDTO: leaderboardUserDTOs)
        {
            returned.add(leaderboardUserDTO.getLeaderboardUserId());
        }
        return returned;
    }
}
