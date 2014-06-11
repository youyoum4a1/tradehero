package com.tradehero.th.api.leaderboard.def;

import android.content.Context;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKeyKnowledge;

public class ExchangeLeaderboardDefDTO extends DrillDownLeaderboardDefDTO
{
    //<editor-fold desc="Constructors">
    public ExchangeLeaderboardDefDTO(Context context)
    {
        super();
        id = LeaderboardDefKeyKnowledge.EXCHANGE_ID;
        name = context.getString(R.string.leaderboard_community_by_exchange);
    }
    //</editor-fold>
}
