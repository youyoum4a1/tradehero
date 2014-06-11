package com.tradehero.th.models.leaderboard.def;

import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTOFactory;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTOList;
import com.tradehero.th.models.DTOProcessor;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorLeaderboardDefDTOList implements DTOProcessor<LeaderboardDefDTOList>
{
    private final LeaderboardDefDTOFactory leaderboardDefDTOFactory;

    public DTOProcessorLeaderboardDefDTOList(@NotNull LeaderboardDefDTOFactory leaderboardDefDTOFactory)
    {
        this.leaderboardDefDTOFactory = leaderboardDefDTOFactory;
    }

    @Override public LeaderboardDefDTOList process(@NotNull LeaderboardDefDTOList value)
    {
        leaderboardDefDTOFactory.complementServerLeaderboardDefDTOs(value);
        return value;
    }
}
