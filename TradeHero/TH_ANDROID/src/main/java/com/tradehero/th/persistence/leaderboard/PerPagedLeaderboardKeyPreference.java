package com.tradehero.th.persistence.leaderboard;

import android.content.Context;
import android.content.SharedPreferences;
import com.tradehero.th.api.leaderboard.key.PerPagedLeaderboardKey;
import java.util.Set;
import javax.inject.Inject;

public class PerPagedLeaderboardKeyPreference extends PagedLeaderboardKeyPreference
{
    public PerPagedLeaderboardKeyPreference(Context context, SharedPreferences preference, String key, Set<String> defaultValue)
    {
        super(context, preference, key, defaultValue);
    }

    public PerPagedLeaderboardKey getPerPagedLeaderboardKey()
    {
        Set<String> set = get();
        if (set == null)
        {
            return null;
        }
        return new PerPagedLeaderboardKey(set, createDefaultValues());
    }

    @Override public PerPagedLeaderboardKey createDefaultValues()
    {
        return new PerPagedLeaderboardKey(Integer.MIN_VALUE, PerPagedLeaderboardKey.FIRST_PAGE, null);
    }
}
