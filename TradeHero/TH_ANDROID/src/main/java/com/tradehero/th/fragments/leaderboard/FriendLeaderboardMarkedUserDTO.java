package com.tradehero.th.fragments.leaderboard;

import android.support.annotation.NonNull;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;

public class FriendLeaderboardMarkedUserDTO extends FriendLeaderboardUserDTO
{
    @NonNull public LeaderboardUserDTO stocksLeaderboardUserDTO;

    //<editor-fold desc="Constructors">
    public FriendLeaderboardMarkedUserDTO(boolean expanded, @NonNull LeaderboardUserDTO stocksLeaderboardUserDTO)
    {
        super(expanded);
        this.stocksLeaderboardUserDTO = stocksLeaderboardUserDTO;
    }
    //</editor-fold>
}
