package com.tradehero.th.fragments.leaderboard.main;

import com.tradehero.th.api.leaderboard.key.ConnectedLeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.DrillDownLeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.MostSkilledLeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.TimePeriodLeaderboardDefListKey;
import javax.inject.Inject;

class MainLeaderboardDefListKeyFactory
{
    //<editor-fold desc="Constructors">
    @Inject public MainLeaderboardDefListKeyFactory()
    {
        super();
    }
    //</editor-fold>

    public LeaderboardDefListKey createFrom(LeaderboardCommunityType leaderboardCommunityType)
    {
        switch (leaderboardCommunityType)
        {
            case Competition:
                return null;
            case Connected:
                return new ConnectedLeaderboardDefListKey();
            case SkillAndCountry:
                return new MostSkilledLeaderboardDefListKey();
            case TimeRestricted:
                return new TimePeriodLeaderboardDefListKey();
            case DrillDown:
                return new DrillDownLeaderboardDefListKey();

            default:
                throw new IllegalArgumentException("Unhandled LeaderboardCommunityType." + leaderboardCommunityType);
        }
    }
}
