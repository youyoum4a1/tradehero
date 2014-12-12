package com.tradehero.th.fragments.leaderboard;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import com.tradehero.th.R;

public enum LeaderboardTabType
{
    STOCKS(R.string.leaderboard_type_stocks, LeaderboardMarkUserListFragment.class),
    FX(R.string.leaderboard_type_fx, LeaderboardMarkUserListFragment.class);

    @StringRes public final int titleRes;
    @NonNull public final Class<? extends Fragment> tabClass;

    private LeaderboardTabType(@StringRes int titleRes, @NonNull Class<? extends Fragment> tabClass)
    {
        this.titleRes = titleRes;
        this.tabClass = tabClass;
    }
}
