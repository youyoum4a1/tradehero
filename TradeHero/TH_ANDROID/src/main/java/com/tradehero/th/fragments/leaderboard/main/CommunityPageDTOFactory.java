package com.tradehero.th.fragments.leaderboard.main;

import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTOList;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.MostSkilledLeaderboardDefListKey;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefListCache;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

import timber.log.Timber;

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

    @NotNull public LeaderboardDefDTOList collectFromCaches(@Nullable String countryCode)
    {
        @NotNull LeaderboardDefDTOList collected = new LeaderboardDefDTOList();
        @Nullable LeaderboardDefListKey key;
        @Nullable LeaderboardDefDTOList cached;
        for (LeaderboardCommunityType type : LeaderboardCommunityType.values())
        {
            key = leaderboardDefListKeyFactory.createFrom(type);
            Timber.e("Type %s, key %s", type, key);
            if (key != null)
            {
                cached = leaderboardDefListCache.get(key);
                if (cached != null)
                {
                    collected.addAll(cached);
                }
                if (countryCode != null && key.equals(new MostSkilledLeaderboardDefListKey()))
                {
                    collected.addAll(collectForCountryCodeFromCaches(countryCode));
                }
            }
        }
        return collected;
    }

    @NotNull public LeaderboardDefDTOList collectForCountryCodeFromCaches(@NotNull String countryCode)
    {
        LeaderboardDefDTOList allKeys = leaderboardDefListCache.get(new LeaderboardDefListKey());
        if (allKeys != null)
        {
            return allKeys.keepForCountryCode(countryCode);
        }
        return new LeaderboardDefDTOList();
    }
}
