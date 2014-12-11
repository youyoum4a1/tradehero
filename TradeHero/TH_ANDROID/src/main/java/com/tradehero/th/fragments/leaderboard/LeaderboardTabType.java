package com.tradehero.th.fragments.leaderboard;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import com.tradehero.th.R;
import com.tradehero.th.fragments.contestcenter.ContestCenterActiveFragment;
import com.tradehero.th.fragments.contestcenter.ContestCenterJoinedFragment;

public enum LeaderboardTabType
{
    STOCKS(R.string.contest_center_tab_active, LeaderboardMarkUserListFragment.class),
    FX(R.string.contest_center_tab_joined, LeaderboardMarkUserListFragment.class),
    ALL(R.string.contest_center_tab_joined, LeaderboardMarkUserListFragment.class);

    @StringRes public final int titleRes;
    @NonNull public final Class<? extends Fragment> tabClass;

    private LeaderboardTabType(@StringRes int titleRes, @NonNull Class<? extends Fragment> tabClass)
    {
        this.titleRes = titleRes;
        this.tabClass = tabClass;
    }
}
