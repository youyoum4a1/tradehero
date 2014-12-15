package com.tradehero.th.api.leaderboard.def;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.MostSkilledContainerLeaderboardDefListKey;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;

public class MostSkilledContainerLeaderboardDefDTO extends LeaderboardDefDTO
{
    //<editor-fold desc="Constructors">
    public MostSkilledContainerLeaderboardDefDTO(Context context)
    {
        super();
        id = LeaderboardDefKeyKnowledge.MOST_SKILLED_CONTAINER_ID;
        name = context.getString(R.string.leaderboard_community_most_skilled);
    }
    //</editor-fold>

    @NonNull @Override public LeaderboardDefListKey getLeaderboardDefListKey()
    {
        return new MostSkilledContainerLeaderboardDefListKey();
    }
}
