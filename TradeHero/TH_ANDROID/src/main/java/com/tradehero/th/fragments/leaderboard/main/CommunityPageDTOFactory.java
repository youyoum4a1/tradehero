package com.tradehero.th.fragments.leaderboard.main;

import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTOList;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefKeyList;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.MostSkilledLeaderboardDefListKey;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefListCache;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

class CommunityPageDTOFactory
{
    private final LeaderboardDefListCache leaderboardDefListCache;
    private final LeaderboardDefCache leaderboardDefCache;
    private final MainLeaderboardDefListKeyFactory leaderboardDefListKeyFactory;

    //<editor-fold desc="Constructors">
    @Inject CommunityPageDTOFactory(
            LeaderboardDefListCache leaderboardDefListCache,
            LeaderboardDefCache leaderboardDefCache,
            MainLeaderboardDefListKeyFactory leaderboardDefListKeyFactory)
    {
        this.leaderboardDefListCache = leaderboardDefListCache;
        this.leaderboardDefCache = leaderboardDefCache;
        this.leaderboardDefListKeyFactory = leaderboardDefListKeyFactory;
    }
    //</editor-fold>

    @NotNull public CommunityPageDTOList collectFromCaches(@Nullable String countryCode)
    {
        CommunityPageDTOList collected = new CommunityPageDTOList();
        LeaderboardDefListKey key;
        for (LeaderboardCommunityType type : LeaderboardCommunityType.values())
        {
            key = leaderboardDefListKeyFactory.createFrom(type);
            if (key != null)
            {
                try
                {
                    collected.addAllLeaderboardDefDTO(
                            leaderboardDefCache.get(
                                    leaderboardDefListCache.get(key)));
                }
                catch (Throwable throwable)
                {
                    Timber.e(throwable, null);
                }
                if (countryCode != null && key.equals(new MostSkilledLeaderboardDefListKey()))
                {
                    collected.addAllLeaderboardDefDTO(collectForCountryCodeFromCaches(countryCode));
                }
            }
        }
        return collected;
    }

    @NotNull public LeaderboardDefDTOList collectForCountryCodeFromCaches(@NotNull String countryCode)
    {
        LeaderboardDefKeyList allKeys = leaderboardDefListCache.get(new LeaderboardDefListKey());
        if (allKeys != null)
        {
            return leaderboardDefCache.get(allKeys).keepForCountryCode(countryCode);
        }
        else
        {
            return new LeaderboardDefDTOList();
        }
    }
}
