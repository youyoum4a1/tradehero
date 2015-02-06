package com.tradehero.th.fragments.leaderboard.main;

import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTOList;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefListCache;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

class CommunityPageDTOFactory
{
    @NotNull private final LeaderboardDefListCache leaderboardDefListCache;
    @NotNull private final MainLeaderboardDefListKeyFactory leaderboardDefListKeyFactory;

    //<editor-fold desc="Constructors">
    @Inject CommunityPageDTOFactory(
            @NotNull LeaderboardDefListCache leaderboardDefListCache,
            @NotNull MainLeaderboardDefListKeyFactory leaderboardDefListKeyFactory)
    {
        this.leaderboardDefListCache = leaderboardDefListCache;
        this.leaderboardDefListKeyFactory = leaderboardDefListKeyFactory;
    }
    //</editor-fold>

    @NotNull public LeaderboardDefDTOList collectForCountryCodeFromCaches(@NotNull String countryCode)
    {
        LeaderboardDefDTOList allKeys = leaderboardDefListCache.get(new LeaderboardDefListKey());
        if (allKeys != null)
        {
            return allKeys.keepForCountryCode(countryCode);
        }
        else
        {
            return new LeaderboardDefDTOList();
        }
    }
}
