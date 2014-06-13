package com.tradehero.th.persistence.leaderboard;

import android.content.Context;
import android.content.SharedPreferences;
import com.tradehero.th.api.leaderboard.key.PagedLeaderboardKey;
import java.util.Set;
import javax.inject.Inject;

public class PagedLeaderboardKeyPreference extends LeaderboardKeyPreference
{
    public PagedLeaderboardKeyPreference(Context context, SharedPreferences preference, String key, Set<String> defaultValue)
    {
        super(context, preference, key, defaultValue);
    }

    public PagedLeaderboardKey getPagedLeaderboardKey()
    {
        Set<String> set = get();
        if (set == null)
        {
            return null;
        }
        return new PagedLeaderboardKey(set, createDefaultValues());
    }

    @Override public PagedLeaderboardKey createDefaultValues()
    {
        return new PagedLeaderboardKey(Integer.MIN_VALUE, PagedLeaderboardKey.FIRST_PAGE);
    }
}
