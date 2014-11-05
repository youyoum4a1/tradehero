package com.tradehero.th.models.leaderboard.def;

import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTOFactory;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTOList;
import com.tradehero.th.models.DTOProcessor;
import android.support.annotation.NonNull;
import rx.functions.Func1;

public class DTOProcessorLeaderboardDefDTOList implements DTOProcessor<LeaderboardDefDTOList>,
        Func1<LeaderboardDefDTOList, LeaderboardDefDTOList>
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

    @Override public LeaderboardDefDTOList call(@NonNull LeaderboardDefDTOList leaderboardDefDTOs)
    {
        return process(leaderboardDefDTOs);
    }
}
