package com.tradehero.th.api.leaderboard;

import com.tradehero.th.api.leaderboard.key.LeaderboardUserId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class LeaderboardUserDTOUtil
{
    @Inject public LeaderboardUserDTOUtil()
    {
    }

    public Map<LeaderboardUserId, LeaderboardUserDTO> map(List<LeaderboardUserDTO> leaderboardUserDTOs)
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

    public List<LeaderboardUserId> getIds(List<LeaderboardUserDTO> leaderboardUserDTOs)
    {
        if (leaderboardUserDTOs == null)
        {
            return null;
        }

        List<LeaderboardUserId> returned = new ArrayList<>();
        for (LeaderboardUserDTO leaderboardUserDTO: leaderboardUserDTOs)
        {
            returned.add(leaderboardUserDTO.getLeaderboardUserId());
        }
        return returned;
    }
}
