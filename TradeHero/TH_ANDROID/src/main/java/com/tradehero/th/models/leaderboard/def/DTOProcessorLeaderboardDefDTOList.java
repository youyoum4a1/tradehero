package com.tradehero.th.models.leaderboard.def;

import android.support.annotation.NonNull;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTOFactory;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTOList;
import com.tradehero.th.models.ThroughDTOProcessor;

public class DTOProcessorLeaderboardDefDTOList extends ThroughDTOProcessor<LeaderboardDefDTOList>
{
    private final LeaderboardDefDTOFactory leaderboardDefDTOFactory;

    public DTOProcessorLeaderboardDefDTOList(@NonNull LeaderboardDefDTOFactory leaderboardDefDTOFactory)
    {
        this.leaderboardDefDTOFactory = leaderboardDefDTOFactory;
    }

    @Override public LeaderboardDefDTOList process(@NonNull LeaderboardDefDTOList value)
    {
        leaderboardDefDTOFactory.complementServerLeaderboardDefDTOs(value);
        return value;
    }
}
