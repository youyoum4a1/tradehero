package com.tradehero.th.persistence.leaderboard;

import android.content.Context;
import android.content.SharedPreferences;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.key.PerPagedFilteredLeaderboardKey;
import com.tradehero.th.api.leaderboard.key.PerPagedLeaderboardKey;
import java.util.Set;
import javax.inject.Inject;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class PerPagedFilteredLeaderboardKeyPreference extends PerPagedLeaderboardKeyPreference
{
    public PerPagedFilteredLeaderboardKeyPreference(
            @NonNull Context context,
            @NonNull SharedPreferences preference,
            @NonNull String key,
            @NonNull Set<String> defaultValue)
    {
        super(context, preference, key, defaultValue);
    }

    @NonNull public PerPagedFilteredLeaderboardKey getPerPagedFilteredLeaderboardKey()
    {
        return new PerPagedFilteredLeaderboardKey(get(), createDefaultValues());
    }

    @Override @NonNull public PerPagedFilteredLeaderboardKey createDefaultValues()
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
