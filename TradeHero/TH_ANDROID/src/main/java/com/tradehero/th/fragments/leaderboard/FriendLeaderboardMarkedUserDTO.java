package com.tradehero.th.fragments.leaderboard;

import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import android.support.annotation.NonNull;

public class FriendLeaderboardMarkedUserDTO extends FriendLeaderboardUserDTO
{
    @NonNull public LeaderboardUserDTO leaderboardUserDTO;

    //<editor-fold desc="Constructors">
    public FriendLeaderboardMarkedUserDTO(boolean expanded, @NonNull LeaderboardUserDTO leaderboardUserDTO)
    {
        super(expanded);
        this.leaderboardUserDTO = leaderboardUserDTO;
    }
    //</editor-fold>
}
