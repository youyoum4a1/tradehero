package com.tradehero.th.models.leaderboard;

import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class LeaderboardDefDTOKnowledge
{
    @NotNull
    private final LeaderboardDefKeyKnowledge leaderboardDefKeyKnowledge;

    //<editor-fold desc="Constructors">
    @Inject public LeaderboardDefDTOKnowledge(@NotNull LeaderboardDefKeyKnowledge leaderboardDefKeyKnowledge)
    {
        this.leaderboardDefKeyKnowledge = leaderboardDefKeyKnowledge;
    }
    //</editor-fold>

    public int getLeaderboardDefIcon(@NotNull LeaderboardDefDTO leaderboardDefDTO)
    {
        return leaderboardDefKeyKnowledge.getLeaderboardDefIcon(leaderboardDefDTO.getLeaderboardDefKey());
        // TODO check the exchanges in description
    }
}
