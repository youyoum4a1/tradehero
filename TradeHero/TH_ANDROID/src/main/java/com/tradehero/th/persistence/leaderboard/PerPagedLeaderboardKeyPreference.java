package com.tradehero.th.persistence.leaderboard;

import android.content.SharedPreferences;
import com.tradehero.th.api.leaderboard.key.PerPagedLeaderboardKey;
import java.util.Set;
import javax.inject.Inject;

/**
 * Created by xavier on 2/13/14.
 */
public class PerPagedLeaderboardKeyPreference extends PagedLeaderboardKeyPreference
{
    public static final String TAG = PerPagedLeaderboardKeyPreference.class.getSimpleName();

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
