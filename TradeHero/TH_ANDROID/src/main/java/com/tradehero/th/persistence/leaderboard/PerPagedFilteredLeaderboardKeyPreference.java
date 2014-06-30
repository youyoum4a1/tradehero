package com.tradehero.th.persistence.leaderboard;

import android.content.Context;
import android.content.SharedPreferences;
import com.tradehero.thm.R;
import com.tradehero.th.api.leaderboard.key.PerPagedFilteredLeaderboardKey;
import com.tradehero.th.api.leaderboard.key.PerPagedLeaderboardKey;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public class PerPagedFilteredLeaderboardKeyPreference extends PerPagedLeaderboardKeyPreference
{
    public PerPagedFilteredLeaderboardKeyPreference(
            @NotNull Context context,
            @NotNull SharedPreferences preference,
            @NotNull String key,
            @NotNull Set<String> defaultValue)
    {
        super(context, preference, key, defaultValue);
    }

    @NotNull public PerPagedFilteredLeaderboardKey getPerPagedFilteredLeaderboardKey()
    {
        return new PerPagedFilteredLeaderboardKey(get(), createDefaultValues());
    }

    @Override @NotNull public PerPagedFilteredLeaderboardKey createDefaultValues()
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
