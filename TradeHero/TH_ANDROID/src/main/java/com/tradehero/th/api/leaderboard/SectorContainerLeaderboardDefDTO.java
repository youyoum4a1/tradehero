package com.tradehero.th.api.leaderboard;

import android.content.Context;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.def.DrillDownLeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKeyKnowledge;

public class SectorContainerLeaderboardDefDTO extends DrillDownLeaderboardDefDTO
{
    //<editor-fold desc="Constructors">
    public SectorContainerLeaderboardDefDTO(Context context)
    {
        super();
        id = LeaderboardDefKeyKnowledge.SECTOR_ID;
        name = context.getString(R.string.leaderboard_community_by_sector);
    }
    //</editor-fold>
}
