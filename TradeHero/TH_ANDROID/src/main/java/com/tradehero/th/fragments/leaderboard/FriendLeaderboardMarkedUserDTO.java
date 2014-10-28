package com.tradehero.th.fragments.leaderboard;

import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;

public class FriendLeaderboardMarkedUserDTO extends FriendLeaderboardUserDTO
{
    public LeaderboardUserDTO leaderboardUserDTO;

    //<editor-fold desc="Constructors">
    public FriendLeaderboardMarkedUserDTO(boolean expanded, LeaderboardUserDTO leaderboardUserDTO)
    {
        super(expanded);
        this.leaderboardUserDTO = leaderboardUserDTO;
    }
    //</editor-fold>
}
