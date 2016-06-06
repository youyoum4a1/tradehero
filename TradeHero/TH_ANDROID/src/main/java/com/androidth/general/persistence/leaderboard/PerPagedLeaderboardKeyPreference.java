package com.androidth.general.persistence.leaderboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.androidth.general.api.leaderboard.key.PerPagedLeaderboardKey;
import java.util.Set;

public class PerPagedLeaderboardKeyPreference extends PagedLeaderboardKeyPreference
{
    public PerPagedLeaderboardKeyPreference(
            @NonNull Context context,
            @NonNull SharedPreferences preference,
            @NonNull String key,
            @NonNull Set<String> defaultValue)
    {
        super(context, preference, key, defaultValue);
    }

    @NonNull public PerPagedLeaderboardKey getPerPagedLeaderboardKey()
    {
        return new PerPagedLeaderboardKey(get(), createDefaultValues());
    }

    @Override @NonNull public PerPagedLeaderboardKey createDefaultValues()
    {
        return new PerPagedLeaderboardKey(Integer.MIN_VALUE, PerPagedLeaderboardKey.FIRST_PAGE, null);
    }
}
