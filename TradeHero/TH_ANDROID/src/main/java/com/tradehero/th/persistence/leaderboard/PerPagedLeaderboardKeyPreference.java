package com.tradehero.th.persistence.leaderboard;

import android.content.SharedPreferences;
import com.tradehero.th.api.leaderboard.key.PerPagedLeaderboardKey;
import java.util.Set;
import javax.inject.Inject;

public class PerPagedLeaderboardKeyPreference extends PagedLeaderboardKeyPreference
{
    @Inject public PerPagedLeaderboardKeyPreference(SharedPreferences preference, String key, Set<String> defaultValue)
    {
        super(preference, key, defaultValue);
    }

    public PerPagedLeaderboardKey getPerPagedLeaderboardKey()
    {
        Set<String> set = get();
        if (set == null)
        {
            return null;
        }
        return new PerPagedLeaderboardKey(set);
    }
}
