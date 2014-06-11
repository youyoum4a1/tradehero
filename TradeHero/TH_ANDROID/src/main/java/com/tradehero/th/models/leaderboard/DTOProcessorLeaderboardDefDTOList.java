package com.tradehero.th.models.leaderboard;

import com.tradehero.th.api.leaderboard.LeaderboardDefDTOFactory;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTOList;
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
