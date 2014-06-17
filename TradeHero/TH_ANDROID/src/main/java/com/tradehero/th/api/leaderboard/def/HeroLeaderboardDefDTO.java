package com.tradehero.th.api.leaderboard.def;

import android.content.Context;
import com.tradehero.th.R;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;

public class HeroLeaderboardDefDTO extends ConnectedLeaderboardDefDTO
{
    //<editor-fold desc="Constructors">
    public HeroLeaderboardDefDTO(Context context)
    {
        super();
        id = LeaderboardDefKeyKnowledge.HERO_ID;
        name = context.getString(R.string.leaderboard_community_heros);
    }
    //</editor-fold>
}
