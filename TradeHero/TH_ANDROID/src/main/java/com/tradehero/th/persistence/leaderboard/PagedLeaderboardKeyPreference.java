package com.tradehero.th.persistence.leaderboard;

import android.content.SharedPreferences;
import com.tradehero.th.api.leaderboard.key.PagedLeaderboardKey;
import java.util.Set;
import javax.inject.Inject;

/**
 * Created by xavier on 2/13/14.
 */
public class PagedLeaderboardKeyPreference extends LeaderboardKeyPreference
{
    @Inject public PagedLeaderboardKeyPreference(SharedPreferences preference, String key, Set<String> defaultValue)
    {
        super(preference, key, defaultValue);
    }

    public PagedLeaderboardKey getPagedLeaderboardKey()
    {
        Set<String> set = get();
        if (set == null)
        {
            return null;
        }
        return new PagedLeaderboardKey(set);
    }
}
