package com.androidth.general.persistence.leaderboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.prefs.StringSetPreference;
import com.androidth.general.api.leaderboard.key.LeaderboardKey;
import java.util.Set;

public class LeaderboardKeyPreference extends StringSetPreference
{
    @NonNull protected final Context context;

    //<editor-fold desc="Constructor">
    public LeaderboardKeyPreference(
            @NonNull Context context,
            @NonNull SharedPreferences preference,
            @NonNull String key,
            @NonNull Set<String> defaultValue)
    {
        super(preference, key, defaultValue);
        this.context = context;
    }
    //</editor-fold>

    @NonNull public LeaderboardKey getLeaderboardKey()
    {
        return new LeaderboardKey(get(), createDefaultValues());
    }

    @NonNull public LeaderboardKey createDefaultValues()
    {
        return new LeaderboardKey(Integer.MIN_VALUE);
    }

    public void set(LeaderboardKey perLeaderboardKey)
    {
        super.set(perLeaderboardKey.getFilterStringSet());
    }
}
