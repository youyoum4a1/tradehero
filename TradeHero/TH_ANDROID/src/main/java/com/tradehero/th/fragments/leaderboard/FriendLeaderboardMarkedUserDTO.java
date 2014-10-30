package com.tradehero.th.fragments.leaderboard;

import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import org.jetbrains.annotations.NotNull;

public class FriendLeaderboardMarkedUserDTO extends FriendLeaderboardUserDTO
{
    @NotNull public LeaderboardUserDTO leaderboardUserDTO;

    //<editor-fold desc="Constructors">
    public FriendLeaderboardMarkedUserDTO(boolean expanded, @NotNull LeaderboardUserDTO leaderboardUserDTO)
    {
        super(expanded);
        this.leaderboardUserDTO = leaderboardUserDTO;
    }
    //</editor-fold>
}
