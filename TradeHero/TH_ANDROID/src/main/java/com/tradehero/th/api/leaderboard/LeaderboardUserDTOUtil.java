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
    public Map<LeaderboardUserId, StocksLeaderboardUserDTO> map(@NonNull List<StocksLeaderboardUserDTO> stocksLeaderboardUserDTOs)
    {
        Map<LeaderboardUserId, StocksLeaderboardUserDTO> returned = new HashMap<>();
        for (StocksLeaderboardUserDTO stocksLeaderboardUserDTO : stocksLeaderboardUserDTOs)
        {
            returned.put(stocksLeaderboardUserDTO.getLeaderboardUserId(), stocksLeaderboardUserDTO);
        }
        return returned;
    }
}
