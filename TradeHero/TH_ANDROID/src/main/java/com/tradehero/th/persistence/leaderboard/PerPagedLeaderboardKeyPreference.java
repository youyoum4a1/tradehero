package com.tradehero.th.persistence.leaderboard;

import android.content.Context;
import android.content.SharedPreferences;
import com.tradehero.th.api.leaderboard.key.PerPagedLeaderboardKey;
import java.util.Set;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PerPagedLeaderboardKeyPreference extends PagedLeaderboardKeyPreference
{
    public PerPagedLeaderboardKeyPreference(
            @NotNull Context context,
            @NotNull SharedPreferences preference,
            @NotNull String key,
            @NotNull Set<String> defaultValue)
    {
        super(context, preference, key, defaultValue);
    }

    @NotNull public PerPagedLeaderboardKey getPerPagedLeaderboardKey()
    {
        return new PerPagedLeaderboardKey(get(), createDefaultValues());
    }

    @Override @NotNull public PerPagedLeaderboardKey createDefaultValues()
    {
        return new PerPagedLeaderboardKey(Integer.MIN_VALUE, PerPagedLeaderboardKey.FIRST_PAGE, null);
    }
}
