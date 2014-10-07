package com.tradehero.th.fragments.contestcenter;

import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import com.tradehero.th.R;
import org.jetbrains.annotations.NotNull;

public enum ContestCenterTabType
{
    ACTIVE(R.string.contest_center_tab_active, ContestCenterActiveFragment.class),
    JOINED(R.string.contest_center_tab_joined, ContestCenterJoinedFragment.class);

    @StringRes public final int titleRes;
    @NotNull public final Class<? extends Fragment> tabClass;

    private ContestCenterTabType(@StringRes int titleRes, @NotNull Class<? extends Fragment> tabClass)
    {
        this.titleRes = titleRes;
        this.tabClass = tabClass;
    }
}
