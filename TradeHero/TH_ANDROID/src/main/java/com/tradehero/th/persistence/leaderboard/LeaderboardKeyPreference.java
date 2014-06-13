package com.tradehero.th.persistence.leaderboard;

import android.content.Context;
import android.content.SharedPreferences;
import com.tradehero.common.persistence.prefs.StringSetPreference;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import java.util.Set;
import javax.inject.Inject;

public class LeaderboardKeyPreference extends StringSetPreference
{
    protected final Context context;

    //<editor-fold desc="Constructor">
    public LeaderboardKeyPreference(Context context, SharedPreferences preference, String key, Set<String> defaultValue)
    {
        super(preference, key, defaultValue);
        this.context = context;
    }
    //</editor-fold>

    public LeaderboardKey getLeaderboardKey()
    {
        Set<String> set = get();
        if (set == null)
        {
            return null;
        }
        return new LeaderboardKey(set, createDefaultValues());
    }

    public LeaderboardKey createDefaultValues()
    {
        return new LeaderboardKey(Integer.MIN_VALUE);
    }

    public void set(LeaderboardKey perLeaderboardKey)
    {
        super.set(perLeaderboardKey.getFilterStringSet());
    }
}
