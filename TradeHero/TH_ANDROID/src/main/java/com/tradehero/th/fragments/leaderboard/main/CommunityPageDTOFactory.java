package com.tradehero.th.fragments.leaderboard.main;

import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefListCache;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

class CommunityPageDTOFactory
{
    private final LeaderboardDefListCache leaderboardDefListCache;
    private final LeaderboardDefCache leaderboardDefCache;

    //<editor-fold desc="Constructors">
    @Inject CommunityPageDTOFactory(
            LeaderboardDefListCache leaderboardDefListCache,
            LeaderboardDefCache leaderboardDefCache)
    {
        this.leaderboardDefListCache = leaderboardDefListCache;
        this.leaderboardDefCache = leaderboardDefCache;
    }
    //</editor-fold>

    @NotNull public CommunityPageDTOList collectFromCaches()
    {
        CommunityPageDTOList collected = new CommunityPageDTOList();
        for (LeaderboardCommunityType type : LeaderboardCommunityType.values())
        {
            if (type.getKey() != null)
            {
                try
                {
                    collected.addAllLeaderboardDefDTO(
                            leaderboardDefCache.get(
                                    leaderboardDefListCache.get(type.getKey())));
                }
                catch (Throwable throwable)
                {
                    Timber.e(throwable, null);
                }
            }
        }
        return collected;
    }
}
