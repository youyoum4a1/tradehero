package com.tradehero.th.fragments.contestcenter;

import android.support.v4.app.Fragment;
import com.tradehero.th.R;

public enum ContestCenterTabType
{
    ACTIVIE(R.string.contest_center_tab_active, ContestCenterActiveFragment.class),
    JOINED(R.string.contest_center_tab_joined, ContestCenterJoinedFragment.class);

    public final int titleRes;
    public final Class<? extends Fragment> tabClass;

    private ContestCenterTabType(int titleRes, Class<? extends Fragment> tabClass)
    {
        this.titleRes = titleRes;
        this.tabClass = tabClass;
    }

}

