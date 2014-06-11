package com.tradehero.th.models.leaderboard;

import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.market.Country;
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
        int byKey = leaderboardDefKeyKnowledge.getLeaderboardDefIcon(leaderboardDefDTO.getLeaderboardDefKey());
        if (byKey != 0)
        {
            return byKey;
        }
        if (leaderboardDefDTO.countryCodes == null)
        {
            return 0;
        }
        Country fromCode;
        for (String countryCode : leaderboardDefDTO.countryCodes)
        {
            try
            {
                return Country.valueOf(countryCode).logoId;
            }
            catch (IllegalArgumentException e)
            {
                e.printStackTrace();
            }
        }
        return 0;
    }
}
