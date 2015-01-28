package com.tradehero.th.fragments.leaderboard.main;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTOList;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.MostSkilledLeaderboardDefListKey;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefListCacheRx;
import javax.inject.Inject;
import timber.log.Timber;

/** TODO IMHO, CommunityPageDTOFactory hides the fact about where the real data comes from */
@Deprecated
class CommunityPageDTOFactory
{
    private static final boolean ENABLE_COUNTRY_LEADERBOARD_DEF = false;
    @NonNull private final LeaderboardDefListCacheRx leaderboardDefListCache;
    @NonNull private final MainLeaderboardDefListKeyFactory leaderboardDefListKeyFactory;

    //<editor-fold desc="Constructors">
    @Inject CommunityPageDTOFactory(
            @NonNull LeaderboardDefListCacheRx leaderboardDefListCache,
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
                cached = leaderboardDefListCache.getCachedValue(key);
                if (cached != null)
                {
                    collected.addAll(cached);
                }
                if (ENABLE_COUNTRY_LEADERBOARD_DEF && countryCode != null && key.equals(new MostSkilledLeaderboardDefListKey(1)))
                {
                    collected.addAll(collectForCountryCodeFromCaches(countryCode));
                }
            }
        }
        return collected;
    }

    @NonNull public LeaderboardDefDTOList collectForCountryCodeFromCaches(@NonNull String countryCode)
    {
        LeaderboardDefDTOList allKeys = leaderboardDefListCache.getCachedValue(new LeaderboardDefListKey(1));
        if (allKeys != null)
        {
            return allKeys.keepForCountryCode(countryCode);
        }
        return new LeaderboardDefDTOList();
    }
}
