package com.tradehero.th.api.leaderboard;

import android.content.Context;
import com.tradehero.th.R;

public class HeroLeaderboardDefDTO extends ConnectedLeaderboardDefDTO
{
    //<editor-fold desc="Constructors">
    public HeroLeaderboardDefDTO(Context context)
    {
        super();
        id = LeaderboardDefDTO.LEADERBOARD_HERO_ID;
        name = context.getString(R.string.leaderboard_community_heros);
    }
    //</editor-fold>
}
