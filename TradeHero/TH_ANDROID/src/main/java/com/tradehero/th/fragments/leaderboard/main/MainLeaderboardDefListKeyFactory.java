package com.tradehero.th.fragments.leaderboard.main;

import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKeyFactory;
import javax.inject.Inject;

class MainLeaderboardDefListKeyFactory extends LeaderboardDefListKeyFactory
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
                return createConnected();
            case SkillAndCountry:
                return createMostSkilled();
            case TimeRestricted:
                return createTimePeriod();
            case DrillDown:
                return createDrillDown();

            default:
                throw new IllegalArgumentException("Unhandled LeaderboardCommunityType." + leaderboardCommunityType);
        }
    }
}
