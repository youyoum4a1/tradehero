package com.tradehero.th.models.leaderboard.def;

import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTOFactory;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTOList;
import com.tradehero.th.models.DTOProcessor;
import org.jetbrains.annotations.NotNull;
import rx.functions.Func1;

public class DTOProcessorLeaderboardDefDTOList implements DTOProcessor<LeaderboardDefDTOList>,
        Func1<LeaderboardDefDTOList, LeaderboardDefDTOList>
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

    @Override public LeaderboardDefDTOList call(@NotNull LeaderboardDefDTOList leaderboardDefDTOs)
    {
        return process(leaderboardDefDTOs);
    }
}
