package com.tradehero.th.fragments.leaderboard.main;

import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTOList;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.MostSkilledLeaderboardDefListKey;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefListCache;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import timber.log.Timber;

class CommunityPageDTOFactory
{
    @NonNull private final LeaderboardDefListCache leaderboardDefListCache;
    @NonNull private final MainLeaderboardDefListKeyFactory leaderboardDefListKeyFactory;

    //<editor-fold desc="Constructors">
    @Inject CommunityPageDTOFactory(
            @NonNull LeaderboardDefListCache leaderboardDefListCache,
            @NonNull MainLeaderboardDefListKeyFactory leaderboardDefListKeyFactory)
    {
        this.leaderboardDefListCache = leaderboardDefListCache;
        this.leaderboardDefListKeyFactory = leaderboardDefListKeyFactory;
    }
    //</editor-fold>

    @NonNull public LeaderboardDefDTOList collectFromCaches(@Nullable String countryCode)
    {
        LeaderboardDefDTOList collected = new LeaderboardDefDTOList();
        LeaderboardDefListKey key;
        LeaderboardDefDTOList cached;
        for (LeaderboardCommunityType type : LeaderboardCommunityType.values())
        {
            key = leaderboardDefListKeyFactory.createFrom(type);
            Timber.d("Type %s, key %s", type, key);
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

    @NonNull public LeaderboardDefDTOList collectForCountryCodeFromCaches(@NonNull String countryCode)
    {
        LeaderboardDefDTOList allKeys = leaderboardDefListCache.get(new LeaderboardDefListKey());
        if (allKeys != null)
        {
            return allKeys.keepForCountryCode(countryCode);
        }
        return new LeaderboardDefDTOList();
    }
}
