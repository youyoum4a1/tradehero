package com.androidth.general.persistence.leaderboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.androidth.general.api.leaderboard.key.PagedLeaderboardKey;
import java.util.Set;

public class PagedLeaderboardKeyPreference extends LeaderboardKeyPreference
{
    public PagedLeaderboardKeyPreference(
            @NonNull Context context,
            @NonNull SharedPreferences preference,
            @NonNull String key,
            @NonNull Set<String> defaultValue)
    {
        super(context, preference, key, defaultValue);
    }

    @NonNull public PagedLeaderboardKey getPagedLeaderboardKey()
    {
        return new PagedLeaderboardKey(get(), createDefaultValues());
    }

    @Override @NonNull public PagedLeaderboardKey createDefaultValues()
    {
        return new PagedLeaderboardKey(Integer.MIN_VALUE, PagedLeaderboardKey.FIRST_PAGE);
    }
}
