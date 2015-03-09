package com.tradehero.th.fragments.leaderboard.main;

import android.support.annotation.NonNull;
import com.tradehero.th.api.leaderboard.key.ConnectedLeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.DrillDownLeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.ExchangeLeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.MostSkilledLeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.TimePeriodLeaderboardDefListKey;

class MainLeaderboardDefListKeyFactory
{
    @NonNull public static LeaderboardDefListKey createFrom(
            @NonNull LeaderboardCommunityType leaderboardCommunityType)
    {
        switch (leaderboardCommunityType)
        {
            case Connected:
                return new ConnectedLeaderboardDefListKey(1);
            case SkillAndCountry:
                return new MostSkilledLeaderboardDefListKey(1);
            case TimeRestricted:
                return new TimePeriodLeaderboardDefListKey(1);
            case DrillDown:
                return new ExchangeLeaderboardDefListKey(1);

            default:
                throw new IllegalArgumentException("Unhandled LeaderboardCommunityType." + leaderboardCommunityType);
        }
    }
}
