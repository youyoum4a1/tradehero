package com.tradehero.th.persistence.leaderboard;

import android.content.SharedPreferences;
import com.tradehero.th.api.leaderboard.key.PerPagedFilteredLeaderboardKey;
import java.util.Set;
import javax.inject.Inject;


public class PerPagedFilteredLeaderboardKeyPreference extends PerPagedLeaderboardKeyPreference
{
    public static final String TAG = PerPagedFilteredLeaderboardKeyPreference.class.getSimpleName();

    @Inject public PerPagedFilteredLeaderboardKeyPreference(SharedPreferences preference, String key, Set<String> defaultValue)
    {
        super(preference, key, defaultValue);
    }

    public PerPagedFilteredLeaderboardKey getPerPagedFilteredLeaderboardKey()
    {
        Set<String> set = get();
        if (set == null)
        {
            return null;
        }
        return new PerPagedFilteredLeaderboardKey(set);
    }
}
