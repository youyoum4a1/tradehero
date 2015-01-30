package com.tradehero.th.api.leaderboard.def;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.th.R;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;

public class ExchangeContainerLeaderboardDefDTO extends DrillDownLeaderboardDefDTO
{
    //<editor-fold desc="Constructors">
    public ExchangeContainerLeaderboardDefDTO(@NonNull Context context)
    {
        super();
        id = LeaderboardDefKeyKnowledge.EXCHANGE_ID;
        name = context.getString(R.string.leaderboard_community_by_exchange);
    }
    //</editor-fold>
}
