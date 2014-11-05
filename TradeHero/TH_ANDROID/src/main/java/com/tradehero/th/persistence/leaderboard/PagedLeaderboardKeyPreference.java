package com.tradehero.th.persistence.leaderboard;

import android.content.Context;
import android.content.SharedPreferences;
import com.tradehero.th.api.leaderboard.key.PagedLeaderboardKey;
import java.util.Set;
import javax.inject.Inject;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
