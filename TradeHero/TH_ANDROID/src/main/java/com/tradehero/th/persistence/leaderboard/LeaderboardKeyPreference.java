package com.tradehero.th.persistence.leaderboard;

import android.content.Context;
import android.content.SharedPreferences;
import com.tradehero.common.persistence.prefs.StringSetPreference;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public class LeaderboardKeyPreference extends StringSetPreference
{
    @NotNull protected final Context context;

    //<editor-fold desc="Constructor">
    public LeaderboardKeyPreference(
            @NotNull Context context,
            @NotNull SharedPreferences preference,
            @NotNull String key,
            @NotNull Set<String> defaultValue)
    {
        super(preference, key, defaultValue);
        this.context = context;
    }
    //</editor-fold>

    @NotNull public LeaderboardKey getLeaderboardKey()
    {
        return new LeaderboardKey(get(), createDefaultValues());
    }

    @NotNull public LeaderboardKey createDefaultValues()
    {
        return new LeaderboardKey(Integer.MIN_VALUE);
    }

    public void set(LeaderboardKey perLeaderboardKey)
    {
        super.set(perLeaderboardKey.getFilterStringSet());
    }
}
