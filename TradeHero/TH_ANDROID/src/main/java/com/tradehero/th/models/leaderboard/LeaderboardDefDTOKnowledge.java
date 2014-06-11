package com.tradehero.th.models.leaderboard;

import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;
import java.util.ArrayList;
import java.util.List;
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

    @NotNull
    public List<Integer> getLeaderboardDefIcon(@NotNull LeaderboardDefDTO leaderboardDefDTO)
    {
        List<Integer> iconResIds = new ArrayList<>();
        int byKey = leaderboardDefKeyKnowledge.getLeaderboardDefIcon(leaderboardDefDTO.getLeaderboardDefKey());
        if (byKey != 0)
        {
            iconResIds.add(byKey);
        }
        else if (leaderboardDefDTO.countryCodes != null)
        {
            Country fromCode;
            for (String countryCode : leaderboardDefDTO.countryCodes)
            {
                try
                {
                    iconResIds.add(Country.valueOf(countryCode).logoId);
                }
                catch (IllegalArgumentException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return iconResIds;
    }
}
