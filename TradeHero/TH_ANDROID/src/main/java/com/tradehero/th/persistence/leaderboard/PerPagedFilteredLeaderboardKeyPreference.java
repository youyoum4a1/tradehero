package com.tradehero.th.persistence.leaderboard;

import android.content.Context;
import android.content.SharedPreferences;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.key.PerPagedFilteredLeaderboardKey;
import com.tradehero.th.api.leaderboard.key.PerPagedLeaderboardKey;
import java.util.Set;
import javax.inject.Inject;

public class PerPagedFilteredLeaderboardKeyPreference extends PerPagedLeaderboardKeyPreference
{
    public PerPagedFilteredLeaderboardKeyPreference(Context context, SharedPreferences preference, String key, Set<String> defaultValue)
    {
        super(context, preference, key, defaultValue);
    }

    public PerPagedFilteredLeaderboardKey getPerPagedFilteredLeaderboardKey()
    {
        Set<String> set = get();
        if (set == null)
        {
            return null;
        }
        return new PerPagedFilteredLeaderboardKey(set, createDefaultValues());
    }

    @Override public PerPagedFilteredLeaderboardKey createDefaultValues()
    {
        return new PerPagedFilteredLeaderboardKey(
                Integer.MIN_VALUE,
                PerPagedLeaderboardKey.FIRST_PAGE,
                null,
                (float) context.getResources().getInteger(R.integer.leaderboard_filter_win_ratio_default),
                (float) context.getResources().getInteger(R.integer.leaderboard_filter_monthly_activity_default),
                (float) context.getResources().getInteger(R.integer.leaderboard_filter_holding_period_default),
                (float) context.getResources().getInteger(R.integer.leaderboard_filter_relative_performance_default),
                (float) context.getResources().getInteger(R.integer.leaderboard_filter_consistency_default));
    }
}
