package com.tradehero.th.models.leaderboard;

import android.support.annotation.NonNull;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class LeaderboardDefDTOKnowledge
{
    @NonNull private final LeaderboardDefKeyKnowledge leaderboardDefKeyKnowledge;

    //<editor-fold desc="Constructors">
    @Inject public LeaderboardDefDTOKnowledge(@NonNull LeaderboardDefKeyKnowledge leaderboardDefKeyKnowledge)
    {
        this.leaderboardDefKeyKnowledge = leaderboardDefKeyKnowledge;
    }
    //</editor-fold>

    @NonNull
    public List<Integer> getLeaderboardDefIcon(@NonNull LeaderboardDefDTO leaderboardDefDTO)
    {
        List<Integer> iconResIds = new ArrayList<>();
        Integer byKey = leaderboardDefKeyKnowledge.getLeaderboardDefIcon(leaderboardDefDTO.getLeaderboardDefKey());
        if (byKey != null)
        {
            iconResIds.add(byKey);
        }
        else if (leaderboardDefDTO.countryCodes != null)
        {
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
