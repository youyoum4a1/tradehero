package com.tradehero.th.persistence.leaderboard;

import android.content.Context;
import android.content.SharedPreferences;
import com.tradehero.th.api.leaderboard.key.PagedLeaderboardKey;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class PagedLeaderboardKeyPreference extends LeaderboardKeyPreference
{
    public PagedLeaderboardKeyPreference(
            @NotNull Context context,
            @NotNull SharedPreferences preference,
            @NotNull String key,
            @NotNull Set<String> defaultValue)
    {
        super(context, preference, key, defaultValue);
    }


    @Override @NotNull public PagedLeaderboardKey createDefaultValues()
    {
        return new PagedLeaderboardKey(Integer.MIN_VALUE, PagedLeaderboardKey.FIRST_PAGE);
    }
}
