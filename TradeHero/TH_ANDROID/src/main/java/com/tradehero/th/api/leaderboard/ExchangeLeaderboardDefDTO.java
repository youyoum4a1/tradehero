package com.tradehero.th.api.leaderboard;

import android.content.Context;
import com.tradehero.th.R;

public class ExchangeLeaderboardDefDTO extends DrillDownLeaderboardDefDTO
{
    //<editor-fold desc="Constructors">
    public ExchangeLeaderboardDefDTO(Context context)
    {
        super();
        id = LeaderboardDefDTO.LEADERBOARD_DEF_EXCHANGE_ID;
        name = context.getString(R.string.leaderboard_community_by_exchange);
    }
    //</editor-fold>
}
