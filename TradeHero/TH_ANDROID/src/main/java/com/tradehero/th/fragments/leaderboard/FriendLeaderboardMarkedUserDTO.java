package com.tradehero.th.fragments.leaderboard;

import android.support.annotation.NonNull;
import com.tradehero.th.api.leaderboard.StocksLeaderboardUserDTO;

public class FriendLeaderboardMarkedUserDTO extends FriendLeaderboardUserDTO
{
    @NonNull public StocksLeaderboardUserDTO stocksLeaderboardUserDTO;

    //<editor-fold desc="Constructors">
    public FriendLeaderboardMarkedUserDTO(boolean expanded, @NonNull StocksLeaderboardUserDTO stocksLeaderboardUserDTO)
    {
        super(expanded);
        this.stocksLeaderboardUserDTO = stocksLeaderboardUserDTO;
    }
    //</editor-fold>
}
